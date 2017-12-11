package io.github.t3r1jj.develog.model.monitor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class Call {
    private String name;
    private int callCount = 0;
    private long accumulatedCallTime = 0;
    private long logTime;

    public Call(String name) {
        this.name = name;
    }

    public void accumulateCall(long callTime) {
        accumulatedCallTime += callTime;
        callCount++;
    }

    public long getCallTime() {
        return (this.accumulatedCallTime / this.callCount);
    }
}
