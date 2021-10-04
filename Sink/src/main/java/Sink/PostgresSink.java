package Sink;

import org.apache.pulsar.client.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostgresSink implements Sink {

    //Sidecar configuration
    private final Conf conf;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<LogEntry> producer;

    boolean keepPulling;

    public static SinkBuilder builder(){
        return new SinkBuilder();
    }

    protected PostgresSink(Conf conf){
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

    private synchronized Consumer<LogEntry> getProducer() throws PulsarClientException {
        if (producer != null) {
            return producer;
        }

        producer = getClient()
                    .newConsumer(Schema.JSON(LogEntry.class))
                    .topic(conf.getSinkTopic())
                    .subscriptionName("postgres_sink")
                    .subscriptionType(SubscriptionType.Shared)
                    .batchReceivePolicy(BatchReceivePolicy.DEFAULT_POLICY)
                    .subscribe();

        return producer;
    }

    public void start() throws Exception {
        Consumer<LogEntry> consumer = getProducer();
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2", "xviewer", "xviewer")){

            while(keepPulling){
            Messages<LogEntry> logEntries = consumer.batchReceive();
            for(Message<LogEntry> msg : logEntries){
                //TODO isolate the database insertion
                LogEntry entry = msg.getValue();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
                    //TODO remove string concatenation
                    LocalDateTime t = LocalDateTime.parse(entry.getDate() + " " + entry.getTime(),formatter);

                    String sql = "INSERT INTO facts.xviewerlogs VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement st = conn.prepareStatement(sql);
                    st.setTimestamp(1, Timestamp.valueOf(t));
                    //st.setString(1, "2015-01-10 00:51:14"); //timest
                    st.setString(2, entry.getEnvironment()); // env
                    st.setString(3, entry.getTechnology()); // tech
                    st.setString(4, entry.getInstance()); // instance
                    st.setString(5, entry.getUuid()); // uuid
                    st.setString(6, entry.getFileName()); // filename
                    st.setLong(7, entry.getSeqNumber()); // seqNumber
                    st.setString(8, entry.getSeverity()); // severity
                    st.setString(9, entry.getThreadName()); // threadName
                    st.setString(10, entry.getCategory()); // category
                    st.setString(11, entry.getMessage()); // message
                    st.setString(12, entry.getRawMessage()); // raw message
                    st.executeUpdate();

            }
        }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }



    public void stop() {
        this.keepPulling = false;
    }

}
