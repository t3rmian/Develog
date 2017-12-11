package io.github.t3r1jj.develog.component;

import io.github.t3r1jj.develog.aspect.CallMonitoringAspect;
import io.github.t3r1jj.develog.model.monitoring.Call;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CallMonitoringTask {
    private final CallMonitoringAspect callMonitoringAspect;
    private final CallRepository callRepository;

    @Autowired
    public CallMonitoringTask(CallMonitoringAspect callMonitoringAspect, CallRepository callRepository) {
        this.callMonitoringAspect = callMonitoringAspect;
        this.callRepository = callRepository;
    }

    @Scheduled(fixedRateString = "${io.github.t3r1jj.develog.monitoring.call.rate})")
    public void dumpToDb() {
        synchronized (callMonitoringAspect) {
            HashMap<String, Call> logs = callMonitoringAspect.getLogs();
            callRepository.saveAll(logs.values());
            callMonitoringAspect.reset();
        }
    }
}
