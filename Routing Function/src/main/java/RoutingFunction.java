import com.google.gson.Gson;
import entities.GenericProperty;
import entities.IndexProperty;
import entities.LogEntry;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.slf4j.Logger;

//This function parses log entries from Xviewer Server
public class RoutingFunction implements Function<LogEntry, Void> {

    public static final Schema<IndexProperty> INDEX_PROPERTY_SCHEMA = Schema.JSON(IndexProperty.class);

    public static final Gson gson = new Gson();
    public Void process(LogEntry input, Context context) throws Exception{
        //Used to send logs to a specified topic
        Logger log = context.getLogger();
        String properties = input.getProperties();

        try{

            if(properties == null){
                return null;
            }

            GenericProperty gprop = gson.fromJson(properties,GenericProperty.class);

            if(gprop.getType().equals(IndexProperty.PROPERTY_TYPE_1) ||
                    gprop.getType().equals(IndexProperty.PROPERTY_TYPE_2)){

                routeToIndexerMetricProcessor(properties,context,log);
            }
        } catch (Exception e){
            log.error(properties);
            log.error(e.toString());

        }

        return null;
    }

    //Send entries to the Apache Pulsar Parsed Topic with a Json schema for the entities.LogEntry
    private void routeToIndexerMetricProcessor(String properties, Context context, Logger log) throws Exception{
        IndexProperty entry = gson.fromJson(properties,IndexProperty.class);
        TypedMessageBuilder<IndexProperty> msgBuilder =
                context.newOutputMessage("index_metrics_processing",INDEX_PROPERTY_SCHEMA);
        MessageId msgId = msgBuilder
                            .value(entry)
                            .key(entry.getKey())
                            .send();

        log.info("Routing Property to indexer processor " + properties);
    }

}