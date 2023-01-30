package common.messages;

import java.time.LocalDateTime;


// This class was going to be used to check if the services are down or not
public class Ack {
    private final AckDetails details;
    private final String target;
    private final LocalDateTime dateTime;
    private final Message message;

    public Ack(AckDetails details, String target, Message message) {
        this.details = details;
        this.target = target;
        this.dateTime = LocalDateTime.now();
        this.message = message;
    }

    public AckDetails getDetails() {
        return details;
    }

    public String getTarget() {
        return target;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Message getMessage() {
        return message;
    }
}
