package IndexAggregator;

public class Conf {

    private String topic;

    private String serviceURL;

    private String DB_user;
    private String DB_password;
    private String DB_url;

    protected Conf(){
        serviceURL = "pulsar://localhost:6650";
        topic = "test";
        DB_user = "xviewer";
        DB_password = "xviewer";
        DB_url = "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2";
    }

    public String getDB_url() {
        return DB_url;
    }

    public void setDB_url(String DB_url) {
        this.DB_url = DB_url;
    }

    public String getDB_user() {
        return DB_user;
    }

    public void setDB_user(String DB_user) {
        this.DB_user = DB_user;
    }

    public String getDB_password() {
        return DB_password;
    }

    public void setDB_password(String DB_password) {
        this.DB_password = DB_password;
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
