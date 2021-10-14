import com.google.gson.Gson;
import entities.IndexProperty;
import entities.IndexerMetric;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

//This function parses log entries from Xviewer Server
public class MetricProcessorFunction implements Function<IndexProperty, Void> {

    public static final Schema<IndexerMetric> INDEX_METRIC_SCHEMA = Schema.JSON(IndexerMetric.class);
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

    public static final Gson gson = new Gson();
    public Void process(IndexProperty input, Context context) throws Exception{
        Logger log = context.getLogger();
        ByteBuffer byteBuffer = context.getState(input.getKey());

        if(byteBuffer == null){
            log.info("Trying to add a new IndexProperty entry");
            addFirstAction(input,context,log);
        }else{
            log.info("Trying to calculate metric value for index: " + input.getIndex());
            calculateMetricValue(input,byteBuffer,context,log);
        }

        //context.putState();

        return null;
    }

    private void calculateMetricValue(IndexProperty secondProp,ByteBuffer byteBuffer, Context context, Logger log)
            throws Exception{
        String jsonString = new String(byteBuffer.array());
        IndexProperty firstProp = gson.fromJson(jsonString,IndexProperty.class);

        String startAsString = firstProp.getInitial_timestamp();
        String finishAsString = secondProp.getInitial_timestamp();


        LocalDateTime f = LocalDateTime.parse(startAsString,formatter);
        LocalDateTime s = LocalDateTime.parse(finishAsString,formatter);

        long timeBetween = ChronoUnit.MILLIS.between(f,s);
        
        log.info(String.format("Toke %d for type: %s index: %s",timeBetween,secondProp.getType(),secondProp.getIndex()));

        IndexerMetric metric = new IndexerMetric();
        metric.setTimestamp(finishAsString);
        metric.setMetricTime(timeBetween);
        metric.setMetricType(firstProp.getType());
        metric.setIndexer(firstProp.getIndex());

        context.deleteState(secondProp.getKey());

        routeToIndexerMetricProcessor(metric,context,log);
        //TODO add verification to confirm type matching

        /*if(input.getType().equals(IndexProperty.PROPERTY_TYPE_1)){

        }else if(input.getType().equals(IndexProperty.PROPERTY_TYPE_2)){

        }*/
    }

    public void addFirstAction(IndexProperty input, Context context, Logger log){
        List<String> actions = input.getAction();
        if(actions.size() != 1){
            log.error("Invalid IndexProperty: No action or multiple actions provided");
            return;
        }

        String firstAction = actions.get(0);

        if(firstAction.equals(IndexProperty.ENQUEING_INDEXING) ||
            firstAction.equals(IndexProperty.NOTIFY_START_PROCESSING_OF_INDEX)){
            //TODO: we could add a validate method to check the validity of the IndexProperty
            String indexPropertyJson = gson.toJson(input);
            ByteBuffer bf = ByteBuffer.wrap(indexPropertyJson.getBytes());
            context.putState(input.getKey(),bf);
            log.info(String.format("Added IndexProperty for action: %s index: %s",input.getAction(),input.getIndex()));
        }else{
            log.error("Invalid IndexProperty: Invalid first action");
        }
    }


    //Send entries to the Apache Pulsar to be stored in the database
    private void routeToIndexerMetricProcessor(IndexerMetric metric, Context context, Logger log) throws Exception{
        TypedMessageBuilder<IndexerMetric> msgBuilder =
                context.newOutputMessage("metrics_sink", INDEX_METRIC_SCHEMA);

        MessageId msgId = msgBuilder
                            .value(metric)
                            .send();

        log.info(String.format("Routing Metric to metric sink for index:%s and action:%s with elapsed_time:%d ",
                metric.getIndexer(),metric.getMetricType(),metric.getMetricTime()));
    }

}