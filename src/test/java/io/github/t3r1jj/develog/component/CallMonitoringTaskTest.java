package io.github.t3r1jj.develog.component;

import io.github.t3r1jj.develog.aspect.CallMonitoringAspect;
import io.github.t3r1jj.develog.model.monitoring.Call;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CallMonitoringTaskTest {

    @Mock
    private CallMonitoringAspect callMonitoringAspect;
    @Mock
    private CallRepository callRepository;

    private CallMonitoringTask callMonitoringTask;
    private final Call call = new Call("test");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(callMonitoringAspect.getLogs()).thenReturn(new HashMap<String, Call>() {{
            this.put(call.getName(), call);
            this.put(call.getName() + "2", call);
        }});
        callMonitoringTask = new CallMonitoringTask(callMonitoringAspect, callRepository);
    }

    @Test
    void dumpToDb() {
        callMonitoringTask.dumpToDb();
        verify(callRepository).saveAll(callMonitoringAspect.getLogs().values());
    }

}