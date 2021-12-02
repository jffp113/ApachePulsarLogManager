package Sink;

import Sink.entities.LogEntry;
import io.prometheus.client.Summary;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClientException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Loader implements Runnable{


    static final Summary requestLatency = Summary.build()
            .name("requests_start_to_logsink_latency_miliseconds")
            .help("Request latency between start to log sink in miliseconds.").register();


    private final Context ctx;
    private final Messages<LogEntry> logEntries;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

    public Loader(Context ctx, Messages<LogEntry> logEntries) {
        this.ctx = ctx;
        this.logEntries = logEntries;
    }

    @Override
    public void run() {
        LogEntry entry = null;
        //Check invariant
        if(logEntries.size() == 0){
            return;
        }
        try {
            String sql = "INSERT INTO facts.xviewerlogs VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = ctx.conn.prepareStatement(sql);
            for(Message<LogEntry> msg : logEntries){
                entry = msg.getValue();

                LocalDateTime t = LocalDateTime.parse(entry.getDate() + " " + entry.getTime(),formatter);

                st.setTimestamp(1, Timestamp.valueOf(t));
                st.setString(2, entry.getEnvironment()); // env
                st.setString(3, entry.getTechnology()); // tech
                st.setString(4, entry.getInstance()); // instance
                st.setString(5, entry.getUuid()); // uuid
                st.setString(6, entry.getFileName()); // filename
                st.setLong(7, entry.getSeqNumber()); // seqNumber
                st.setString(8, entry.getSeverity()); // severity
                st.setString(9, entry.getThreadName()); // threadName
                st.setString(10, entry.getCategory()); // category
                st.setString(11, entry.getMessage()); // message

                if(entry.getProperties() == null) {
                    st.setString(12, "");// properties
                } else{
                    st.setString(12, entry.getProperties()); // properties
                }

                st.setString(13, entry.getRawMessage()); // raw message
                st.addBatch();

                long elapsed_time = ChronoUnit.MILLIS.between(t,LocalDateTime.now());
                requestLatency.observe(elapsed_time);
            }

            st.executeBatch();
            ctx.ackMessages(logEntries);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n", e.getSQLState(), e.getMessage());
            System.err.format("Entry uuid %s value %s\n",entry.getUuid(),entry.getRawMessage());
        } catch (PulsarClientException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

}
