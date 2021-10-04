import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class FilterFunction implements Function<LogEntry, LogEntry> {

    public static final List<String> filter = Collections.singletonList("ERROR");

    public LogEntry apply(LogEntry input) {
        if(!filter.contains(input.getSeverity())){
            return null;
        }
        return input;
    }
}