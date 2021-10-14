package Alarm;

import Alarm.emailApi.EmailSender;
import Alarm.emailApi.MockEmailSender;
import Alarm.entities.LogEntry;
import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

public class EmailAlarm implements Alarm{

    //Sidecar configuration
    private final Conf conf;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<LogEntry> producer;

    boolean keepPulling;

    public static final BatchReceivePolicy ALARM_BATCH_POLICY = BatchReceivePolicy.builder()
                                                                        .maxNumBytes(10 * 1024 * 1024)
                                                                        .maxNumMessages(-1)
                                                                        .timeout(5,TimeUnit.SECONDS)
                                                                        .build();

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
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionType(SubscriptionType.Shared)
                    .batchReceivePolicy(ALARM_BATCH_POLICY)
                    .subscribe();

        return producer;
    }

    public void start() throws Exception {
        Consumer<LogEntry> consumer = getProducer();
        EmailSender emailSender = new MockEmailSender();
        while(keepPulling){
            Messages<LogEntry> logEntries = consumer.batchReceive();
            if(logEntries.size() != 0){
                emailSender.sendEmail(logEntries);
            }
            consumer.acknowledge(logEntries);
        }
    }

    public void stop() {
        this.keepPulling = false;
    }

}
