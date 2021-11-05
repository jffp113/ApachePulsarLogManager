package IndexAggregator;

import IndexAggregator.entities.AvgMetric;
import IndexAggregator.entities.IndexerMetric;
import IndexAggregator.entities.IndexerPrecision;
import io.prometheus.client.Counter;
import org.apache.pulsar.client.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexAggregatorImp implements IndexAggregator {

    public static final String PRECISION = "PRECISION";

    //Sidecar configuration
    private final Conf conf;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<IndexerMetric> producer;

    boolean keepPulling;


    public static IndexAggregatorBuilder builder(){
        return new IndexAggregatorBuilder();
    }

    protected IndexAggregatorImp(Conf conf){
        this.conf = conf;
        client = null;
        producer = null;
        keepPulling = true;
    }

    private synchronized PulsarClient getClient() throws PulsarClientException {
        if (client != null) {
            return client;
        }

        client = PulsarClient.builder()
                //connect to multiple brokers or a proxy
                .serviceUrl(conf.getServiceURL())
                .build();

            return client;
    }

    private synchronized Consumer<IndexerMetric> getProducer() throws PulsarClientException {
        if (producer != null) {
            return producer;
        }

        producer = getClient()
                    .newConsumer(Schema.JSON(IndexerMetric.class))
                    .topic(conf.getTopic())
                    .subscriptionName("index_aggregator_subs")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionType(SubscriptionType.Key_Shared)
                    .subscribe();

        return producer;
    }

    private final Map<String,AvgMetric> minuteCache = new ConcurrentHashMap<>();
    private final Map<String,AvgMetric> hoursCache = new ConcurrentHashMap<>();
    private final Map<String,AvgMetric> daysCache = new ConcurrentHashMap<>();

    public void start() throws Exception {
        System.out.println("Starting IndexAggregator");
        Consumer<IndexerMetric> consumer = getProducer();
        //TODO credentials should be in env variables with kubernetes secrets
        Connection conn = DriverManager.getConnection(
                conf.getDB_url(), conf.getDB_user(), conf.getDB_password());
        IndexerMetric entry = null;
        Thread cacheFlusherMi = new Thread(new CacheFlusher(minuteCache, IndexerPrecision.MINUTE,conf));
        cacheFlusherMi.start();

        Thread cacheFlusherHr = new Thread(new CacheFlusher(hoursCache, IndexerPrecision.HOUR,conf));
        cacheFlusherHr.start();

        Thread cacheFlusherD = new Thread(new CacheFlusher(daysCache, IndexerPrecision.DAY,conf));
        cacheFlusherD.start();

        while(keepPulling){
            Message<IndexerMetric> msg = consumer.receive();
            IndexerMetric metric = msg.getValue();
            String timePrecisionString = msg.getProperty(PRECISION);

            if(timePrecisionString == null){
                System.out.println("Ignoring message with null precision");
                continue;
            }

            System.out.println(timePrecisionString);
            IndexerPrecision precision = IndexerPrecision.convertPrecisionString(timePrecisionString);
            AvgMetric avg = getAvgValue(conn, msg.getKey(),precision);

            updateAvg(avg,metric.getMetricTime());
            consumer.acknowledge(msg);
        }
        conn.close();

    }

    private void updateAvg(AvgMetric avg, long newValue){
        double newAvg = (avg.getAvg()*avg.getCount() + newValue) / (avg.getCount() + 1);
        System.out.println("Calculated Value " + newAvg);
        avg.setAvg(newAvg);
        avg.setCount(avg.getCount() + 1);
        avg.setUpdated(true);
        avg.setLastUpdate(LocalDateTime.now());
    }

    private AvgMetric getAvgValue(Connection conn, String key, IndexerPrecision precision){
        AvgMetric avg = getFromPrecisionCache(key,precision);
        if(avg != null){
            Metrics.add_cache_hit(precision);
            System.out.println("Value for " + key + "served from cache");
            return avg;
        }
        Metrics.add_cache_misses(precision);
        avg = getFromDatabaseCache(conn,key,precision);
        putOnPrecisionCache(avg,key,precision);
        return avg;
    }

    private AvgMetric getFromPrecisionCache(String key, IndexerPrecision precision){
        switch (precision){
            case MINUTE:
                return minuteCache.get(key);
            case HOUR:
                return hoursCache.get(key);
            case DAY:
                return daysCache.get(key);
        }
        return minuteCache.get(key);
    }

    private void putOnPrecisionCache(AvgMetric value, String key,IndexerPrecision precision){
        System.out.printf("Value %s inserted on cache (%s)",value.getTimest(),precision);
        switch (precision){
            case MINUTE:
                minuteCache.put(key,value);
                break;
            case HOUR:
                hoursCache.put(key,value);
                break;
            case DAY:
                daysCache.put(key,value);
                break;
        }

    }

    private AvgMetric getFromDatabaseCache(Connection conn,String key, IndexerPrecision precision){
        switch (precision){
            case MINUTE:
                return getFromMinutesDatabaseOrDefault(conn,key);
            case HOUR:
                return getFromHoursDatabaseOrDefault(conn,key);
            case DAY:
                return getFromDaysDatabaseOrDefault(conn,key);
            default:
                return null;
        }

    }

    private AvgMetric getFromMinutesDatabaseOrDefault(Connection conn,String key){
        String tableName = System.getenv("MINUTE_DB_TABLE");
        return genericDatabaseOrDefault(conn,key,IndexerPrecision.MINUTE,tableName);
    }

    private AvgMetric getFromHoursDatabaseOrDefault(Connection conn,String key){
        String tableName = System.getenv("HOUR_DB_TABLE");
        return genericDatabaseOrDefault(conn,key,IndexerPrecision.HOUR,tableName);
    }

    private AvgMetric getFromDaysDatabaseOrDefault(Connection conn,String key){
        String tableName = System.getenv("DAY_DB_TABLE");
        return genericDatabaseOrDefault(conn,key,IndexerPrecision.DAY,tableName);
    }

    private AvgMetric genericDatabaseOrDefault(Connection conn,String key,IndexerPrecision precision, String tableName){
        try{
            System.out.println("Getting from the database for " + key);
            PreparedStatement st = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE timest = ?");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
            LocalDateTime t = LocalDateTime.parse(key,formatter);
            st.setTimestamp(1, Timestamp.valueOf(t));
            ResultSet r = st.executeQuery();
            if(r.next()){
                return new AvgMetric(key,r.getString(2), r.getDouble(3),r.getLong(4),
                        LocalDateTime.MIN, precision,false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Value not present in the database");
        return new AvgMetric(key,"",0,0,
                LocalDateTime.now(),precision,false);
    }

    public void stop() {
        this.keepPulling = false;
    }

}
