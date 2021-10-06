package extractor;

import org.apache.pulsar.client.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {

    private static final String TOPIC_PREFIX = "rawlogs-";

    //Sidecar configuration
    private final Conf conf;

    //Apache Pulsar client
    PulsarClient client;

    //Producer
    Producer<String> producer;

    boolean keepReading;

    public static ExtractorBuilder builder(){
        return new ExtractorBuilder();
    }

    protected Extractor(Conf conf){
        this.conf = conf;
        client = null;
        producer = null;
        keepReading = true;
    }

    private synchronized PulsarClient getClient() throws PulsarClientException {
        if (client != null) {
            return client;
        }

        client = PulsarClient.builder()
                //connect to multiple brokers or a proxy
                //e.g pulsar://pulsar-mini-proxy:6650
                .serviceUrl(conf.getServiceURL())
                .build();

            return client;
    }

    private synchronized Producer<String> getProducer() throws PulsarClientException {
        if (producer != null) {
            return producer;
        }

        producer = getClient()
                    .newProducer(Schema.STRING)
                    .topic(TOPIC_PREFIX + conf.getEnvironment() + "-" + conf.getTechnology() + "-" + conf.getInstance())
                    .accessMode(ProducerAccessMode.Exclusive)
                    .create();

        return producer;
    }


    public void start() throws Exception {
        Producer<String> producer = getProducer();

        File file = new File(conf.getFilepath());
        BufferedReader reader = new BufferedReader(new FileReader(file));

        Pattern newLogLinePattern = Pattern.compile(conf.getLineRegex());

        StringBuilder buffer = new StringBuilder();
        while(keepReading){
            String next = reader.readLine();

            if(next == null){
                if(buffer.length() != 0){
                    send(producer,buffer.toString());
                    buffer = new StringBuilder();
                }
                //wait until for more lines
                Thread.sleep(1000);
                continue;
            }

            Matcher m = newLogLinePattern.matcher(next);
            if(m.matches()){
                if(buffer.length() != 0){
                    send(producer,buffer.toString());
                    //Simulate log reading
                    Thread.sleep(conf.getTimeBetweenLineReading());
                    buffer = new StringBuilder();
                }
            }
            buffer.append(next)
                    .append("\n");
        }
    }

    public void send(Producer<String> producer, String value) throws PulsarClientException {
        TypedMessageBuilder<String> msgBuilder = producer.newMessage();
        msgBuilder.property("ENVIRONMENT",conf.getEnvironment())
                .property("INSTANCE",conf.getInstance())
                .property("TECHNOLOGY",conf.getTechnology())
                .property("FILENAME",conf.getFilepath())
                .value(value);

        MessageId msgId = msgBuilder.send();
        System.out.printf("Sending %s: %s", msgId.toString(),value);
    }

    public void stop(){
        keepReading = false;
    }

}
