package IndexAggregator;

import IndexAggregator.entities.AvgMetric;
import IndexAggregator.entities.IndexerPrecision;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFlusher implements Runnable{
    private final Connection conn;
    private final Map<String, AvgMetric> cache;
    private final long FLUSH_TIME;
    private final long CACHE_SIZE;
    private final float EVICT_PERCENTAGE;
    private final String TABLE_NAME;
    private final String TABLE_CONSTRAINT;
    private final IndexerPrecision PRECISION;

    public CacheFlusher(Map<String, AvgMetric> cache, IndexerPrecision precision) throws SQLException {
        conn =  DriverManager.getConnection(
                "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2", "xviewer", "xviewer");
        this.cache = cache;
        String flushTimeString = System.getenv("FLUSH_TIME");
        FLUSH_TIME = Long.parseLong(flushTimeString);

        String cacheSizeString = System.getenv("CACHE_SIZE");
        CACHE_SIZE = Long.parseLong(cacheSizeString);

        String evictPercentageString = System.getenv("EVICT_PERCENTAGE");
        EVICT_PERCENTAGE = Float.parseFloat(evictPercentageString);

        TABLE_NAME = System.getenv(precision + "_DB_TABLE");
        TABLE_CONSTRAINT = System.getenv(precision.toString() + "_TABLE_CONSTRAINT");

        PRECISION = precision;

    }


    @Override
    public void run(){
        boolean continueFlushing = true;
        while(continueFlushing){
            SortedSet<AvgMetric> orderToDelete = new TreeSet<>();

            try {
                LocalDateTime now = LocalDateTime.now().plusHours(1);
                long start = System.currentTimeMillis();

                cache.forEach((k,v) -> {
                    if (v.isUpdated()){
                        insertToGenericTable(conn,v);
                        v.setUpdated(false);
                    }
                    orderToDelete.add(v);
                });

                long elapsed = System.currentTimeMillis() - start;
                insertDBLatency(conn,now,elapsed);

                if(cache.size() > CACHE_SIZE){
                    long nToDelete = (long) (cache.size() * EVICT_PERCENTAGE);
                    System.out.printf("Evicting %d entries from cache", nToDelete);

                    for(AvgMetric v : orderToDelete){
                        if(nToDelete == 0){
                            break;
                        }
                        System.out.println("Evicted " + v.getTimest());
                        cache.remove(v.getTimest());
                        nToDelete--;
                    }
                }

                Thread.sleep(FLUSH_TIME);
            } catch (InterruptedException e) {
                System.out.println("Sleep was interrupted, terminating cache flusher");
                continueFlushing = false;
            }
        }
    }



    private void insertToGenericTable(Connection conn,AvgMetric avg){
        System.out.println("Inserting into the database for " + avg.getTimest());

        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?) " +
                "ON CONFLICT ON CONSTRAINT " + TABLE_CONSTRAINT + " " +
                "DO UPDATE SET avg_metrictime=? , xv_count_metrictime=?";

        try(PreparedStatement st = conn.prepareStatement(sql)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
            LocalDateTime t = LocalDateTime.parse(avg.getTimest(),formatter);

            //Insert
            st.setTimestamp(1, Timestamp.valueOf(t));
            st.setString(2, avg.getMetricType());
            st.setDouble(3, avg.getAvg());
            st.setLong(4, avg.getCount());

            st.setDouble(5, avg.getAvg());
            st.setLong(6, avg.getCount());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //facts.xviewer_indexer_metrics_pulsar_latency
    private void insertDBLatency(Connection conn,LocalDateTime time, Long elapsedTime){
        System.out.println("Took %d to send to the database" + elapsedTime);

        String sql = "INSERT INTO facts.xviewer_indexer_metrics_pulsar_latency VALUES (?, ?) ;";

        try(PreparedStatement st = conn.prepareStatement(sql)){
            //Insert
            st.setTimestamp(1, Timestamp.valueOf(time));
            st.setLong(2, elapsedTime);

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
