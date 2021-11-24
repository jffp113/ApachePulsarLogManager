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

    public SinkBuilder setDBurl(String url) {
        conf.setDB_url(url);
        return this;
    }

    public SinkBuilder setDBuser(String username) {
        conf.setDB_user(username);
        return this;
    }

    public SinkBuilder setDBpassword(String password) {
        conf.setDB_password(password);
        return this;
    }

    public SinkBuilder setBatchMaxBytes(int bytes) {
        conf.setBatchMaxBytes(bytes);
        return this;
    }

    public SinkBuilder setBatchMaxMessages(int messages) {
        conf.setBatchMaxMessages(messages);
        return this;
    }

    public SinkBuilder setBatchTimeout(int timeout) {
        conf.setBatchTimeout(timeout);
        return this;
    }

    public PostgresMetricSink build(){
        return new PostgresMetricSink(conf);
    }

}
