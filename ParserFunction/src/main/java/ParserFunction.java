import com.google.gson.Gson;
import entities.IndexProperty;
import entities.LogEntry;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.slf4j.Logger;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This function parses log entries from Xviewer Server
public class ParserFunction implements Function<String, Void> {

    //E.g 2021-10-04 08:42:34,994 INFO  [com.crossjoin...PersistentAlarmChecker] (Thread-122) ALARM CHECK COMPLETED
    public static final String FIRST_LINE_STRING_REGEX =
                "^(20[0-9]{2}-[0-9]{2}-[0-9]{2})\\s+([0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\s+(INFO|WARN|DEBUG|ERROR)\\s+(\\[.*\\])\\s+(\\((?:[^)(]+|\\((?:[^)(]+|\\([^)(]*\\))*\\))*\\))\\s+([\\S\\s]*)";

    public static final String IS_EXCEPTION = "\\tat.*";

    private static final String ENQUEING_REGEX = "^Enqueing indexing request \\[(.*)\\][\\r\\n|\\r|\\n]";
    private static final String DEQUEUED_REGEX = "^\\d*-> Dequeued request \\[(.*)\\] executing in worker now![\\r\\n|\\r|\\n]";

    private static final String START_INDEX_REGEX = "^(\\d*)-> Notify start processing of index (.*)[\\r\\n|\\r|\\n]";;
    private static final String END_INDEX_REGEX = "^(\\d*)-> Notify finished processing of index (.*)[\\r\\n|\\r|\\n]";

    public static final Schema<LogEntry> LOG_ENTRY_SCHEMA = Schema.JSON(LogEntry.class);//JSONSchema.of(entities.LogEntry.class);
    public static final Schema<String> STRING_SCHEMA = Schema.STRING;

    public static final String PARSED_TOPIC = "parsed_logs";
    public static final String ERROR_LOGS = "unparsed_logs";
    public static final String EXCEPTION_LOGS = "exception_logs";

    private final Pattern newLogLinePattern = Pattern.compile(FIRST_LINE_STRING_REGEX);

    private static long counter = 0; //TODO, should be stateful function getState()

    private void parseLogEntry(LogEntry entry,String line) {
        Matcher match = newLogLinePattern.matcher(line);
        //Check for matches, ignoring result
        match.matches();

        //Convert Date to the appropriate format
        entry.setDate(match.group(1)); //Date
        entry.setTime(match.group(2)); //Time

        entry.setSeverity(match.group(3)); //Severity
        entry.setCategory(match.group(4)); //Category (class name)
        entry.setThreadName(match.group(5)); //Thread
        entry.setMessage(match.group(6)); //Message
        entry.setRawMessage(line);
    }

    private final Pattern exceptionPattern = Pattern.compile(IS_EXCEPTION);
    private boolean isException(String input){
        Matcher match = exceptionPattern.matcher(input);
        return match.find();
    }


    //Parse indexes information
    //E.g
    // Queuing time:
    //      - Enqueing indexing request [236533]
    //      - Dequeued request [236533] executing in worker now!
    //Indexing time:
    //      - 440-> Notify start processing of index 236533
    //      - 440-> Notify finished processing of index 236533
    private final Pattern enqueingIndexingPattern = Pattern.compile(ENQUEING_REGEX);
    private final Pattern dequeuedIndexingPattern = Pattern.compile(DEQUEUED_REGEX);
    private final Pattern startIndexingPattern = Pattern.compile(START_INDEX_REGEX);
    private final Pattern endIndexingPattern = Pattern.compile(END_INDEX_REGEX);
    private boolean parseIndexProperties(LogEntry entry){
        String matchingLine = entry.getMessage();

        if(matchEnqueingIndexing(entry,matchingLine)){
            return true;
        } else if (matchingDequeuedIndexing(entry,matchingLine)){
            return true;
        } else if (matchingStartProcessingIndexing(entry, matchingLine)) {
            return true;
        } else if (matchingEndProcessingIndexing(entry, matchingLine)) {
            return true;
        }

        return false;
    }


    private final Gson gson = new Gson();
    private boolean matchEnqueingIndexing(LogEntry entry, String line){
        Matcher match = enqueingIndexingPattern.matcher(line);
        if(!match.matches()){
            return false;
        }
        IndexProperty prop = IndexProperty.NewEnqueingIndexingProperty(match.group(1),entry.getUuid(),
                entry.getDate() + " " + entry.getTime());
        entry.setProperties(gson.toJson(prop));
        return true;
    }

    private boolean matchingDequeuedIndexing(LogEntry entry, String line){
        Matcher match = dequeuedIndexingPattern.matcher(line);
        if(!match.matches()){
            return false;
        }
        IndexProperty prop = IndexProperty.NewDequeuedIndexingProperty(match.group(1),entry.getUuid(),
                entry.getDate() + " " + entry.getTime());
        entry.setProperties(gson.toJson(prop));
        return true;
    }

    private boolean matchingStartProcessingIndexing(LogEntry entry, String line){
        Matcher match = startIndexingPattern.matcher(line);
        if(!match.matches()){
            return false;
        }
        IndexProperty prop = IndexProperty.StartProcessingIndexingProperty(match.group(2),match.group(1),entry.getUuid(),
                entry.getDate() + " " + entry.getTime());
        entry.setProperties(gson.toJson(prop));
        return true;
    }

    private boolean matchingEndProcessingIndexing(LogEntry entry, String line){
        Matcher match = endIndexingPattern.matcher(line);
        if(!match.matches()){
            return false;
        }
        IndexProperty prop = IndexProperty.EndProcessingIndexingProperty(match.group(2),match.group(1),entry.getUuid(),
                entry.getDate() + " " + entry.getTime());
        entry.setProperties(gson.toJson(prop));
        return true;
    }

    public Void process(String input, Context context) {
        //Used to send logs to a specified topic
        Logger log = context.getLogger();

        //We create an empty
        LogEntry entry =  new LogEntry();
        try {
            log.info("Start parsing new log entry");
            Map<String, String> properties =
                    context.getCurrentRecord().getProperties();

            //context.incrCounter(COUNTER_NAME,1);
            entry.setSeqNumber(counter++); //todo needs to be stateful
            entry.setUuid(Util.genUUID());
            entry.setEnvironment(properties.get("ENVIRONMENT"));
            entry.setInstance(properties.get("INSTANCE"));
            entry.setTechnology(properties.get("TECHNOLOGY"));
            entry.setFileName(properties.get("FILENAME"));

            parseLogEntry(entry,input);
            boolean propertiesParsed = parseIndexProperties(entry);
            log.info("Index properties available:" + propertiesParsed);
            sendParsedLogEntry(entry,context,log);
            if(isException(input)){
                sendExceptionLogEntry(entry,context,log);
            }
        } catch (Exception e) {
            log.error("Error Parsing new entry: " + e);
            sendNotParsedLogEntry(input,context,log);
        }

        log.info("Finished parsing new log entry");
        return null;
    }

    //Send entries to the Apache Pulsar Parsed Topic with a Json schema for the entities.LogEntry
    private void sendParsedLogEntry(LogEntry entry, Context context, Logger log) throws Exception{
        TypedMessageBuilder<LogEntry> msgBuilder =
                context.newOutputMessage(PARSED_TOPIC,LOG_ENTRY_SCHEMA);
        MessageId msgId = msgBuilder.value(entry).send();
        log.info("Published log entry with message id: " + msgId);
    }

    //Send entries to the Apache Pulsar exception Topic with a Json schema for the entities.LogEntry
    private void sendExceptionLogEntry(LogEntry entry, Context context, Logger log) throws Exception{
        TypedMessageBuilder<LogEntry> msgBuilder =
                context.newOutputMessage(EXCEPTION_LOGS,LOG_ENTRY_SCHEMA);
        MessageId msgId = msgBuilder.value(entry).send();
        log.info("Published log entry with message id: " + msgId);
    }

    //Send entries to the Apache Pulsar unparsed Topic with a Json schema for the entities.LogEntry
    private void sendNotParsedLogEntry(String input, Context context, Logger log){
        try{
            TypedMessageBuilder<String> msgBuilder =
                    context.newOutputMessage(ERROR_LOGS,STRING_SCHEMA);
            MessageId msgId = msgBuilder.value(input).send();
            log.warn("Published not parsed log entry with message id: " + msgId.toString());
        }catch(Exception e){
            log.error("Error sending message not parsed log entry: " + input);
        }
    }
}