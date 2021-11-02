import Alarm.EmailAlarm;

public class Main {

    public static void main(String[] args) throws Exception {
        String urlService = System.getenv("URL_SERVICE");
        String alarmTopic = System.getenv("ALARM_TOPIC");
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        EmailAlarm c = EmailAlarm.builder()
                                .setServiceURL(urlService)
                                .setAlarmTopic(alarmTopic)
                                .setUsername(username)
                                .setPassword(password)
                                .build();

        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
