package Sink;

import Sink.entities.LogEntry;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClientException;

public class ExtractorTransformer implements Runnable{

    private final Context ctx;
    public ExtractorTransformer(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        try{
            while(ctx.keepPulling.get()){
                try{
                    Messages<LogEntry> messages = ctx.receiveBatch();
                    if(messages.size() > 0){
                        ctx.putMessagesOnQueue(messages);
                    }
                }catch (PulsarClientException e){
                    System.out.println("Unable to get Batch");
                }
            }
        }catch (InterruptedException e){
            System.out.println("ExtractorTransformer stopped");
        }
    }
}
