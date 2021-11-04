import Sink.PostgresSink;
import io.prometheus.client.exporter.HTTPServer;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("SINK_TOPIC");

        HTTPServer server = new HTTPServer.Builder()
                .withPort(8080)
                .build();

        PostgresSink c = PostgresSink.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
