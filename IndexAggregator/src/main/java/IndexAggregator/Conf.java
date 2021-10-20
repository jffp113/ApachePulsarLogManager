package IndexAggregator;

public class Conf {

    private String topic;

    private String serviceURL;

    protected Conf(){
        serviceURL = "pulsar://localhost:6650";
        topic = "test";
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }
}
