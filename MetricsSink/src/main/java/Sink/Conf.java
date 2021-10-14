package Sink;

public class Conf {

    private String sinkTopic;

    private String serviceURL;

    protected Conf(){
        serviceURL = "pulsar://localhost:6650";
        sinkTopic = "test";
    }

    public String getSinkTopic() {
        return sinkTopic;
    }

    public void setSinkTopic(String sinkTopic) {
        this.sinkTopic = sinkTopic;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }
}
