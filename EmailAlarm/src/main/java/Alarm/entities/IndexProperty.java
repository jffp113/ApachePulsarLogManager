package Alarm.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/*E.g
    {
        type: "IndexerEnqueueMetrics"
        key:  "indexer_{indexerid}"
        uuid: "123123-14234132-1243141"
        action: ["Enqueing indexing request","Dequeued request"]
        initial: "2021-10-07 09:10:45,888"
        final: "2021-10-07 09:10:45,898"
        partial_times: ["2021-10-07 09:10:45,898"]
    }
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndexProperty {

    public static final String PROPERTY_TYPE_1 = "IndexerQueueMetrics";
    public static final String PROPERTY_TYPE_2 = "IndexerProcessingMetrics";


    public static final String ENQUEING_INDEXING = "Enqueing indexing";
    public static final String DEQUEUED_INDEXING = "Dequeued indexing";
    public static final String NOTIFY_START_PROCESSING_OF_INDEX = "Notify start processing of index";
    public static final String NOTIFY_FINISHED_PROCESSING_OF_INDEX = "Notify finished processing of index";
    //type: "IndexerMetrics"
    private String type;
    //key:  "indexer_{indexerid}"
    private String key;

    //index: 143212
    private String index;

    //uuid: "123123-14234132-1243141"
    private String uuid;
    //action: ["Enqueing indexing request","Dequeued request"]
    private List<String> action;
    //initial: "2021-10-07 09:10:45,888"
    private String initial_timestamp;
    //final: "2021-10-07 09:10:45,898"
    private String final_timestamp;
    //partial_times: ["2021-10-07 09:10:45,898"]
    private List<String> finalTimestamp;

    public static IndexProperty NewEnqueingIndexingProperty(String indexerId, String uuid, String timestamp){
        IndexProperty prop = new IndexProperty();
        prop.type = "IndexerQueueMetrics";
        prop.key = "indexer_" + indexerId;
        prop.index = indexerId;
        prop.uuid = uuid;
        prop.action = Arrays.asList(ENQUEING_INDEXING);
        prop.initial_timestamp = timestamp;
        return prop;
    }

    public static IndexProperty NewDequeuedIndexingProperty(String indexerId, String uuid, String timestamp){
        IndexProperty prop = new IndexProperty();
        prop.type = "IndexerQueueMetrics";
        prop.key = "indexer_" + indexerId;
        prop.index = indexerId;
        prop.uuid = uuid;
        prop.action = Arrays.asList(DEQUEUED_INDEXING);
        prop.initial_timestamp = timestamp;
        return prop;
    }

    public static IndexProperty StartProcessingIndexingProperty(String indexerId, String request, String uuid, String timestamp){
        IndexProperty prop = new IndexProperty();
        prop.type = "IndexerProcessingMetrics";
        prop.key = "indexer_" + indexerId + "_" + request;
        prop.index = indexerId;
        prop.uuid = uuid;
        prop.action = Arrays.asList(NOTIFY_START_PROCESSING_OF_INDEX);
        prop.initial_timestamp = timestamp;
        return prop;
    }

    public static IndexProperty EndProcessingIndexingProperty(String indexerId, String request, String uuid, String timestamp){
        IndexProperty prop = new IndexProperty();
        prop.type = "IndexerProcessingMetrics";
        prop.key = "indexer_" + indexerId + "_" + request;
        prop.index = indexerId;
        prop.uuid = uuid;
        prop.action = Arrays.asList(NOTIFY_FINISHED_PROCESSING_OF_INDEX);
        prop.initial_timestamp = timestamp;
        return prop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getAction() {
        return action;
    }

    public void setAction(List<String> action) {
        this.action = action;
    }

    public String getInitial_timestamp() {
        return initial_timestamp;
    }

    public void setInitial_timestamp(String initial_timestamp) {
        this.initial_timestamp = initial_timestamp;
    }

    public String getFinal_timestamp() {
        return final_timestamp;
    }

    public void setFinal_timestamp(String final_timestamp) {
        this.final_timestamp = final_timestamp;
    }

    public List<String> getFinalTimestamp() {
        return finalTimestamp;
    }

    public void setFinalTimestamp(List<String> finalTimestamp) {
        this.finalTimestamp = finalTimestamp;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
