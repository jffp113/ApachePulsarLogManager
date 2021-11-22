package Sink;

public class Conf {

    private String sinkTopic;

    private String serviceURL;

    private String DB_user;
    private String DB_password;
    private String DB_url;

    private int batchMaxMessages;
    private int batchTimeout;
    private int batchMaxBytes;

    protected Conf(){
        serviceURL = "pulsar://localhost:6650";
        sinkTopic = "test";
        DB_user = "xviewer";
        DB_password = "xviewer";
        DB_url = "jdbc:postgresql://host.minikube.internal:5432/xviewer-r2";

        batchMaxBytes = 10 * 1024 * 1024;
        batchTimeout = 100;
        batchMaxMessages = -1;
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

    public String getDB_url() {
        return DB_url;
    }

    public void setDB_url(String DB_url) {
        this.DB_url = DB_url;
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

    public int getBatchMaxMessages() {
        return batchMaxMessages;
    }

    public void setBatchMaxMessages(int batchMaxMessages) {
        this.batchMaxMessages = batchMaxMessages;
    }

    public int getBatchTimeout() {
        return batchTimeout;
    }

    public void setBatchTimeout(int batchTimeout) {
        this.batchTimeout = batchTimeout;
    }

    public int getBatchMaxBytes() {
        return batchMaxBytes;
    }

    public void setBatchMaxBytes(int batchMaxBytes) {
        this.batchMaxBytes = batchMaxBytes;
    }
}
