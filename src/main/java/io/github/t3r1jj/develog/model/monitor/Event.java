package io.github.t3r1jj.develog.model.monitor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Document
@NoArgsConstructor
public class Event {
    @Id
    private String id;
    private long time = Instant.now().toEpochMilli();
    private String description;
    private Type type;
    private Integer value;

    public Event(String description, Type type, Integer value) {
        this.description = description;
        this.type = type;
        this.value = value;
    }

    public Event(Type type, Integer value) {
        this.type = type;
        this.value = value;
    }

    public LocalDateTime getDate() {
        return LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC);
    }

    public enum Type {
        ONLINE
    }
}
