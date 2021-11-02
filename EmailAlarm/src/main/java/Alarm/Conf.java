package Alarm;

public class Conf {

    //Time between new line reading
    private int timeBetweenAlarms;

    private String alarmTopic;

    private String serviceURL;
    private String username;
    private String password;

    protected Conf(){
        timeBetweenAlarms = 0;
        serviceURL = "pulsar://localhost:6650";
        alarmTopic = "test";
    }

    public int getTimeBetweenAlarms() {
        return timeBetweenAlarms;
    }

    public void setTimeBetweenAlarms(int timeBetweenAlarms) {
        this.timeBetweenAlarms = timeBetweenAlarms;
    }

    public String getAlarmTopic() {
        return alarmTopic;
    }

    public void setAlarmTopic(String alarmTopic) {
        this.alarmTopic = alarmTopic;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
