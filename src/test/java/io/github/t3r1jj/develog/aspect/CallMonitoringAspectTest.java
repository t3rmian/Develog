package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.Application;
import io.github.t3r1jj.develog.component.SessionCounter;
import io.github.t3r1jj.develog.model.monitor.Call;
import io.github.t3r1jj.develog.service.MonitoringDao;
import io.github.t3r1jj.develog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class CallMonitoringAspectTest {

    @Mock
    private MonitoringDao monitoringDao;
    @Autowired
    private CallMonitoringAspect callMonitoringAspect;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionCounter sessionCounter;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        callMonitoringAspect.reset();

        Field field = SessionCounter.class.getDeclaredField("monitoringDao");
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(sessionCounter, monitoringDao);
        field.setAccessible(accessible);
    }

    @Test
    void invoke() {
        userService.getUser(1);
        userService.getUser(2);
        userService.getUser(3);
        HashMap<String, Call> logs = callMonitoringAspect.getLogs();
        System.out.println(logs);
        assertEquals(1, logs.size(), "One call log");
        Call call = logs.values().iterator().next();
        assertTrue(call.getName().contains("getUser"), "Call log contains method name");
        assertEquals(3, call.getCallCount(), "Correct call count");
        assertTrue(call.getAccumulatedCallTime() > 0, "Positive call time");
        assertEquals(call.getAccumulatedCallTime() / 3, call.getCallTime(), "Correct call count");
    }

    @Test
    void invokeDisabled() throws NoSuchFieldException, IllegalAccessException {
        Field field = CallMonitoringAspect.class.getDeclaredField("isEnabled");
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(callMonitoringAspect, false);
        field.setAccessible(accessible);

        assertFalse(callMonitoringAspect.isEnabled());
        userService.getUser(1);
        userService.getUser(2);
        userService.getUser(3);
        HashMap<String, Call> logs = callMonitoringAspect.getLogs();
        assertTrue(logs.size() == 0, "One call log");
    }

}