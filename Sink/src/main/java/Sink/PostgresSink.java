package Sink;

import Sink.entities.LogEntry;
import io.prometheus.client.Summary;
import org.apache.pulsar.client.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresSink implements Sink {

    //Sidecar configuration
    private final Conf conf;

    private Context ctx;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<LogEntry> consumer;

    boolean keepPulling;

    static final Summary requestLatency = Summary.build()
            .name("requests_start_to_logsink_latency_miliseconds")
            .help("Request latency between start to log sink in miliseconds.").register();

    private Thread extractorTranformerThread;

    public static SinkBuilder builder(){
        return new SinkBuilder();
    }

    protected PostgresSink(Conf conf){
        this.conf = conf;
        client = null;
        consumer = null;
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

    private synchronized Consumer<LogEntry> getConsumer() throws PulsarClientException {
        if (consumer != null) {
            return consumer;
        }

        BatchReceivePolicy batchPolicy = BatchReceivePolicy.builder()
                                .timeout(conf.getBatchTimeout(), TimeUnit.MILLISECONDS)
                                .maxNumBytes(conf.getBatchMaxBytes())
                                .maxNumMessages(conf.getBatchMaxMessages())
                                .build();

        consumer = getClient()
                    .newConsumer(Schema.JSON(LogEntry.class))
                    .topic(conf.getSinkTopic())
                    .subscriptionName("postgres_sink")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionType(SubscriptionType.Shared)
                    .batchReceivePolicy(batchPolicy)
                    .subscribe();

        return consumer;
    }

    //Extract
    //Transformation
    //Load

    //BlockingQueue tasks = new ArrayBlockingQueue(size)
    //ExecutorService executor = Executors.newFixedThreadPool(10)


    //Extract
    //Just Pull data

    //Transformation and Load
    //Load to the database


    public void start() throws Exception {
        //Consumer<LogEntry> consumer = getConsumer();
        Connection conn = DriverManager.getConnection(
                conf.getDB_url(), conf.getDB_user(), conf.getDB_password());

        BlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(50);
        ExecutorService executor = new ThreadPoolExecutor(5,10,5,TimeUnit.SECONDS,tasks);

        ctx = new Context(getClient(), getConsumer(),new AtomicBoolean(true),conn,tasks,executor);

        ExtractorTransformer extractorTransformer = new ExtractorTransformer(ctx);
        this.extractorTranformerThread = new Thread(extractorTransformer);
        this.extractorTranformerThread.start();


        this.extractorTranformerThread.join();
    }



    public void stop() {

    }

}
