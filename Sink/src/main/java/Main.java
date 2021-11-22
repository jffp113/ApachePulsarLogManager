import Sink.PostgresSink;
import io.prometheus.client.exporter.HTTPServer;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("SINK_TOPIC");
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        int batchMaxMessages = Integer.parseInt(System.getenv("BATCH_MAX_MESSAGES"));
        int batchTimeout = Integer.parseInt(System.getenv("BATCH_TIMEOUT"));
        int batchMaxSize = Integer.parseInt(System.getenv("BATCH_MAX_SIZE"));



        HTTPServer server = new HTTPServer.Builder()
                .withPort(8080)
                .build();

        PostgresSink c = PostgresSink.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .setDBurl(dbUrl)
                                .setDBuser(dbUser)
                                .setDBpassword(dbPassword)
                                .setBatchMaxMessages(batchMaxMessages)
                                .setBatchTimeout(batchTimeout)
                                .setBatchMaxBytes(batchMaxSize)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
