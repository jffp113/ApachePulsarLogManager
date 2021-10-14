package Alarm.emailApi;

import Alarm.entities.LogEntry;
import org.apache.pulsar.client.api.Messages;

public interface EmailSender {
    void sendEmail(Messages<LogEntry> entries) throws Exception;
}
