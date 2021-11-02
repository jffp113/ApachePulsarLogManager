import IndexAggregator.IndexAggregatorImp;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("TOPIC");
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        IndexAggregatorImp c = IndexAggregatorImp.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .setPassword(password)
                                .setUsername(username)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
