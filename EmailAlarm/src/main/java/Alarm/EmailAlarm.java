package Alarm;

import org.apache.pulsar.client.api.*;

public class EmailAlarm implements Alarm{

    //Sidecar configuration
    private final Conf conf;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<LogEntry> producer;

    boolean keepPulling;

    public static AlarmBuilder builder(){
        return new AlarmBuilder();
    }

    protected EmailAlarm(Conf conf){
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
                    .topic(conf.getAlarmTopic())
                    .subscriptionName("email_alarm")
                    .subscriptionType(SubscriptionType.Shared)
                    .batchReceivePolicy(BatchReceivePolicy.DEFAULT_POLICY)
                    .subscribe();

        return producer;
    }

    public void start() throws Exception {
        Consumer<LogEntry> consumer = getProducer();
        while(keepPulling){
            Messages<LogEntry> logEntries = consumer.batchReceive();
            for(Message<LogEntry> e : logEntries){
                System.out.println("Sending Email for: " + e.getValue().getRawMessage());
            }
        }
    }

    public void stop() {
        this.keepPulling = false;
    }

}
