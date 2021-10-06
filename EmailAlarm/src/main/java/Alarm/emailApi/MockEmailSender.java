package Alarm.emailApi;

import Alarm.LogEntry;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Messages;

import java.util.List;

public class MockEmailSender implements EmailSender{


    @Override
    public void sendEmail(Messages<LogEntry> entries) {
        StringBuilder st = new StringBuilder();
        st.append("Sending Email:\n");
        st.append("Number Of entries:\n");
        st.append(entries.size()); st.append("\n");
        for(Message<LogEntry> e : entries){
            st.append("---------------------\n");
            st.append(e.getValue().getRawMessage());
        }
        System.out.println(st);
    }

}
