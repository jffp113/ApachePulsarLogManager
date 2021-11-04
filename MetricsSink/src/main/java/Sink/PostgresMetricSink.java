package Sink;

import Sink.entities.IndexerMetric;
import io.prometheus.client.Summary;
import org.apache.pulsar.client.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PostgresMetricSink implements Sink {

    //Sidecar configuration
    private final Conf conf;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<IndexerMetric> producer;

    boolean keepPulling;

    static final Summary requestLatency = Summary.build()
            .name("requests_start_to_metric_latency_miliseconds")
            .help("Request latency between start to metric sink in miliseconds.").register();

    public static SinkBuilder builder(){
        return new SinkBuilder();
    }

    protected PostgresMetricSink(Conf conf){
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
                    .topic(conf.getSinkTopic())
                    .subscriptionName("postgres_sink")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionType(SubscriptionType.Shared)
                    .batchReceivePolicy(BatchReceivePolicy.DEFAULT_POLICY)
                    .subscribe();

        return producer;
    }

    public void start() throws Exception {
        Consumer<IndexerMetric> consumer = getProducer();
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2", "xviewer", "xviewer");
        IndexerMetric entry = null;
        while(keepPulling){
            try {
                Messages<IndexerMetric> logEntries = consumer.batchReceive();
                for(Message<IndexerMetric> msg : logEntries){
                    //TODO isolate the database insertion
                    entry = msg.getValue();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
                    LocalDateTime t = LocalDateTime.parse(entry.getTimestamp(),formatter);
                    String sql = "INSERT INTO facts.xviewer_indexer_metrics VALUES (?, ?, ?, ?)";

                    PreparedStatement st = conn.prepareStatement(sql);
                    st.setTimestamp(1, Timestamp.valueOf(t));
                    st.setString(2, entry.getMetricType());
                    st.setString(3, entry.getIndexer());
                    st.setLong(4, entry.getMetricTime());

                    st.executeUpdate();

                    System.out.println(ChronoUnit.MILLIS.between(t,LocalDateTime.now()));
                    requestLatency.observe(ChronoUnit.MILLIS.between(t,LocalDateTime.now()));

                    //Ack the message
                    consumer.acknowledge(msg);
                }
            } catch (SQLException e) {
                System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
            }
        }
        conn.close();

    }


    public void stop() {
        this.keepPulling = false;
    }

}
