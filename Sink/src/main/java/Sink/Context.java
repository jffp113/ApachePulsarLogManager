package Sink;

import Sink.entities.LogEntry;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class Context {

    private final ExecutorService executor;

    //Apache Pulsar client
    PulsarClient client;

    //Consumer to read logs
    Consumer<LogEntry> consumer;

    AtomicBoolean keepPulling;

    Connection conn;

    BlockingQueue<Runnable> tasks;

    public Context(PulsarClient client, Consumer<LogEntry> consumer, AtomicBoolean keepPulling, Connection conn,
                   BlockingQueue<Runnable> tasks, ExecutorService executor) {
        this.client = client;
        this.consumer = consumer;
        this.keepPulling = keepPulling;
        this.conn = conn;
        this.tasks = tasks;
        this.executor = executor;
    }

    public Messages<LogEntry> receiveBatch() throws PulsarClientException {
        return consumer.batchReceive();
    }

    public void putMessagesOnQueue(Messages<LogEntry> messages) throws InterruptedException{
        tasks.put(new Loader(this,messages));
    }

    public void ackMessages(Messages<LogEntry> entries) throws PulsarClientException  {
        this.consumer.acknowledge(entries);
    }

    public void close() {
        this.keepPulling.set(false);
        executor.shutdown();
        try{
            conn.close();
        }catch (Exception ignored){}
    }
}
