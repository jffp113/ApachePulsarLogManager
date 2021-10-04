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

    public PostgresSink build(){
        return new PostgresSink(conf);
    }

}
