package Sink;

public class SinkBuilder {

    private final Conf conf;

    protected SinkBuilder(){
        conf = new Conf();
    }

    public SinkBuilder setServiceURL(String serviceURL) {
        conf.setServiceURL(serviceURL);
        return this;
    }

    public SinkBuilder setSinkTopic(String topic) {
        conf.setSinkTopic(topic);
        return this;
    }

    public SinkBuilder setPassword(String password) {
        conf.setPassword(password);
        return this;
    }

    public SinkBuilder setUsername(String username) {
        conf.setUsername(username);
        return this;
    }

    public PostgresMetricSink build(){
        return new PostgresMetricSink(conf);
    }

}
