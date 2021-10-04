import Alarm.EmailAlarm;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String alarmTopic = System.getenv("ALARM_TOPIC");

        EmailAlarm c = EmailAlarm.builder()
                                .setServiceURL(urlService)
                                .setAlarmTopic(alarmTopic)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
