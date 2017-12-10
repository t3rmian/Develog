package io.github.t3r1jj.develog.model.monitoring;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class MethodInfo {
    private String name;
    private int callCount = 0;
    private long accumulatedCallTime = 0;
    private long logTime;

    public MethodInfo(String name) {
        this.name = name;
    }

    public void accumulateCall(long callTime) {
        accumulatedCallTime += callTime;
        callCount++;
    }

    public long getCallTime() {
        return (this.callCount > 0 ? this.accumulatedCallTime / this.callCount : 0);
    }
}
