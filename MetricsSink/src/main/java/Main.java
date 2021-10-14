import Sink.PostgresMetricSink;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("SINK_TOPIC");

        PostgresMetricSink c = PostgresMetricSink.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
