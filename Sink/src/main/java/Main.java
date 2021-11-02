import Sink.PostgresSink;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("SINK_TOPIC");
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        PostgresSink c = PostgresSink.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .setUsername(username)
                                .setPassword(password)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
