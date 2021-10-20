import IndexAggregator.IndexAggregatorImp;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String sinkTopic = System.getenv("TOPIC");

        IndexAggregatorImp c = IndexAggregatorImp.builder()
                                .setServiceURL(urlService)
                                .setSinkTopic(sinkTopic)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
