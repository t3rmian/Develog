package io.github.t3r1jj.develog.model.monitor;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Document
public class Error {
    private long time = Instant.now().toEpochMilli();
    private String name;
    private String stackTrace;
    private String info;

    public Error(String name, String stackTrace, String info) {
        this.name = name;
        this.stackTrace = stackTrace;
        this.info = info;
    }

    public LocalDateTime getDate() {
        return LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC);
    }
}
