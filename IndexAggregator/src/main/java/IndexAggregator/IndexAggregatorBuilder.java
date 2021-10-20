package IndexAggregator;

public class IndexAggregatorBuilder {

    private final Conf conf;

    protected IndexAggregatorBuilder(){
        conf = new Conf();
    }

    public IndexAggregatorBuilder setServiceURL(String serviceURL) {
        conf.setServiceURL(serviceURL);
        return this;
    }

    public IndexAggregatorBuilder setSinkTopic(String topic) {
        conf.setTopic(topic);
        return this;
    }

    public IndexAggregatorImp build(){
        return new IndexAggregatorImp(conf);
    }

}
