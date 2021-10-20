package IndexAggregator.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvgMetric implements Comparable<AvgMetric>{


    public static final String INDEXER_QUEUE_TIME = "IndexerQueueMetrics";
    public static final String INDEXER_PRECESSING_TIME = "IndexerProcessingMetrics";

    private String timest;
    private String metricType;
    private double avg;
    private long count;
    private LocalDateTime lastUpdate;
    private IndexerPrecision precision;
    private boolean updated;

    public String getTimest() {
        return timest;
    }

    public void setTimest(String timest) {
        this.timest = timest;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public IndexerPrecision getPrecision() {
        return precision;
    }

    public void setPrecision(IndexerPrecision precision) {
        this.precision = precision;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public int compareTo(AvgMetric o) {
        return this.lastUpdate.compareTo(o.lastUpdate);
    }

}
