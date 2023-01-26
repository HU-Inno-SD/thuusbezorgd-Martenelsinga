package common;

import java.time.LocalDateTime;

public class Ack {
    private final AckDetails details;
    private final String target;
    private final LocalDateTime dateTime;

    public Ack(AckDetails details, String target) {
        this.details = details;
        this.target = target;
        this.dateTime = LocalDateTime.now();
    }
}
