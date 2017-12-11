package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.Application;
import io.github.t3r1jj.develog.model.monitoring.Call;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class CallMonitoringAspectTest {

    @Autowired
    private CallMonitoringAspect callMonitoringAspect;
    @Autowired
    private ErrorRepository errorRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        callMonitoringAspect.reset();
        errorRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        callMonitoringAspect.reset();
        errorRepository.deleteAll();
    }

    @Test
    void invoke() {
        userService.getUser(1);
        userService.getUser(2);
        userService.getUser(3);
        HashMap<String, Call> logs = callMonitoringAspect.getLogs();
        assertTrue(logs.size() == 1, "One call log");
        Call call = logs.values().iterator().next();
        assertTrue(call.getName().contains("getUser"), "Call log contains method name");
        assertEquals(3, call.getCallCount(), "Correct call count");
        assertTrue(call.getAccumulatedCallTime() > 0, "Positive call time");
        assertEquals(call.getAccumulatedCallTime() / 3, call.getCallTime(), "Correct call count");
    }
    
}