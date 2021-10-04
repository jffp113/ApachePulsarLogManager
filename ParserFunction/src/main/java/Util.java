import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Util {

    public static LocalDateTime ParseDate(String date, String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
        //TODO remove string concatenation
        return LocalDateTime.parse(date + " " + time,formatter);
    }

    public static String genUUID(){
        UUID uuid = UUID.randomUUID();
        return  uuid.toString();
    }

}
