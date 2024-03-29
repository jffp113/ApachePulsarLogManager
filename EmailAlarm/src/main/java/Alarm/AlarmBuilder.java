package Alarm;

public class AlarmBuilder{

    private final Conf conf;

    protected AlarmBuilder(){
        conf = new Conf();
    }

    public AlarmBuilder setServiceURL(String serviceURL) {
        conf.setServiceURL(serviceURL);
        return this;
    }

    public AlarmBuilder setAlarmTopic(String topic) {
        conf.setAlarmTopic(topic);
        return this;
    }

    public AlarmBuilder setTimeBetweenAlarms(int value) {
        conf.setTimeBetweenAlarms(value);
        return this;
    }

    public EmailAlarm build(){
        return new EmailAlarm(conf);
    }

}
