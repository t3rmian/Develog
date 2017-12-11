package io.github.t3r1jj.develog.model.monitoring;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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
}
