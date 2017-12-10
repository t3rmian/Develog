package io.github.t3r1jj.develog.model.monitoring;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
public class EventInfo {
    private long time = Instant.now().toEpochMilli();
    private String description;

    public EventInfo(String description) {
        this.description = description;
    }
}
