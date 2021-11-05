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


    public IndexAggregatorBuilder setDBurl(String url) {
        conf.setDB_url(url);
        return this;
    }

    public IndexAggregatorBuilder setDBuser(String username) {
        conf.setDB_user(username);
        return this;
    }

    public IndexAggregatorBuilder setDBpassword(String password) {
        conf.setDB_password(password);
        return this;
    }


    public IndexAggregatorImp build(){
        return new IndexAggregatorImp(conf);
    }

}
