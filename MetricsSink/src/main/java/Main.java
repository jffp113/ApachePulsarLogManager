import Sink.PostgresMetricSink;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("SINK_TOPIC");
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        System.out.printf("Starting with urlService:%s sinkTopic:%s username:%s password:%s"
                ,urlService,sinkTopic,username,password);
        PostgresMetricSink c = PostgresMetricSink.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .setPassword(password)
                                .setUsername(username)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
