package IndexAggregator;

import IndexAggregator.entities.AvgMetric;
import IndexAggregator.entities.IndexerMetric;
import org.apache.pulsar.client.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexAggregatorImp implements IndexAggregator {

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

    private Map<String,AvgMetric> cache = new ConcurrentHashMap<>();

    public void start() throws Exception {
        System.out.println("Starting IndexAggregator");
        Consumer<IndexerMetric> consumer = getProducer();
        //TODO credentials should be in env variables with kubernetes secrets
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2", "xviewer", "xviewer");
        IndexerMetric entry = null;
        Thread cacheFlusherTh = new Thread(new CacheFlusher(cache));
        cacheFlusherTh.start();

        while(keepPulling){
            Message<IndexerMetric> msg = consumer.receive();
            IndexerMetric metric = msg.getValue();
            AvgMetric avg = getAvgValue(conn, msg.getKey());

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

    private AvgMetric getAvgValue(Connection conn, String key){
        AvgMetric avg = cache.get(key);
        if(avg != null){
            System.out.println("Value for " + key + "served from cache");
            return avg;
        }
        avg = getFromMinutesDatabaseOrDefault(conn,key);
        cache.put(key,avg);
        return avg;
    }


    private AvgMetric getFromMinutesDatabaseOrDefault(Connection conn,String key){
        try{
            System.out.println("Getting from the database for " + key);
            PreparedStatement st = conn.prepareStatement("SELECT * FROM facts.xviewer_indexer_metrics_pulsar_mi WHERE timest = ?");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
            LocalDateTime t = LocalDateTime.parse(key,formatter);
            st.setTimestamp(1, Timestamp.valueOf(t));
            ResultSet r = st.executeQuery();
            if(r.next()){
                return new AvgMetric(key,r.getString(2), r.getDouble(3),r.getLong(4),
                        LocalDateTime.MIN, AvgMetric.IndexerPrecision.MINUTE,false);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Value not present in the database");
        return new AvgMetric(key,"",0,0,
                LocalDateTime.now(), AvgMetric.IndexerPrecision.MINUTE,false);
    }


    public void stop() {
        this.keepPulling = false;
    }

}
