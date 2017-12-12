package io.github.t3r1jj.develog.model.monitor;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Document
public class Event {
    private long time = Instant.now().toEpochMilli();
    private String description;

    public Event(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC);
    }
}
