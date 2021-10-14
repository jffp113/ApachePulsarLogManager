package entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndexerMetric {

    public static final String INDEXER_QUEUE_TIME = "IndexerQueueMetrics";
    public static final String INDEXER_PRECESSING_TIME = "IndexerProcessingMetrics";

    private String timestamp;
    private String metricType;
    private String indexer;
    private long metricTime;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getIndexer() {
        return indexer;
    }

    public void setIndexer(String indexer) {
        this.indexer = indexer;
    }

    public long getMetricTime() {
        return metricTime;
    }

    public void setMetricTime(long metricTime) {
        this.metricTime = metricTime;
    }
}
