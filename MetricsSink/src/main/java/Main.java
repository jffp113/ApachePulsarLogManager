import Sink.PostgresMetricSink;
import io.prometheus.client.exporter.HTTPServer;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("SINK_TOPIC");
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        HTTPServer server = new HTTPServer.Builder()
                .withPort(8080)
                .build();

        PostgresMetricSink c = PostgresMetricSink.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .setDBurl(dbUrl)
                                .setDBuser(dbUser)
                                .setDBpassword(dbPassword)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
