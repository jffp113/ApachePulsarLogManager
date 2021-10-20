package IndexAggregator;

import IndexAggregator.entities.AvgMetric;

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

    public CacheFlusher(Map<String, AvgMetric> cache) throws SQLException {
        conn =  DriverManager.getConnection(
                "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2", "xviewer", "xviewer");
        this.cache = cache;
        String flushTimeString = System.getenv("FLUSH_TIME");
        FLUSH_TIME = Long.parseLong(flushTimeString);

        String cacheSizeString = System.getenv("CACHE_SIZE");
        CACHE_SIZE = Long.parseLong(cacheSizeString);

        String evictPercentageString = System.getenv("EVICT_PERCENTAGE");
        EVICT_PERCENTAGE = Float.parseFloat(evictPercentageString);
    }


    @Override
    public void run(){
        boolean continueFlushing = true;
        while(continueFlushing){
            SortedSet<AvgMetric> orderToDelete = new TreeSet<>();

            try {
                cache.forEach((k,v) -> {
                    if (v.isUpdated()){
                        insertToMinutesDatabase(conn,v);
                        v.setUpdated(false);
                    }
                    orderToDelete.add(v);
                });

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

    private void insertToMinutesDatabase(Connection conn,AvgMetric avg){
        System.out.println("Inserting into the database for " + avg.getTimest());

        String sql = "INSERT INTO facts.xviewer_indexer_metrics_pulsar_mi VALUES (?, ?, ?, ?) " +
                "ON CONFLICT ON CONSTRAINT indexer_metrics_pkey_mi " +
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
}
