package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.monitor.Call;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    @Mock
    private CallRepository callRepository;
    private AdminController adminController;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        adminController = new AdminController();
        MockitoAnnotations.initMocks(this);
        Field field = AdminController.class.getDeclaredField("callRepository");
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(adminController, callRepository);
        field.setAccessible(accessible);
    }

    @Test
    void prepareCallLogs() {
        long oldestCall1Time = Instant.now().minusSeconds(10).toEpochMilli();
        when(callRepository.findAll()).thenReturn(new ArrayList<Call>() {{
            this.add(new Call("call1", 1, 10, oldestCall1Time));
            this.add(new Call("call1", 2, 10, Instant.now().minusSeconds(5).toEpochMilli()));
            this.add(new Call("call2", 3, 10, Instant.now().minusSeconds(5).toEpochMilli()));
        }});
        List<Map<String, String>> logs = adminController.prepareCallLogs("name", "avg",
                "total", "since");
        assertEquals(2, logs.size(), "Two different calls");
        assertEquals("call1", logs.get(0).get("name"), "First call is call1 (longest avg call time)");
        assertEquals(3, Long.parseLong(logs.get(0).get("total")), "Total call count should be correct");
        assertEquals((20 / 3) + " ms", logs.get(0).get("avg"), "Avg time should be correct");
        assertEquals(LocalDateTime.ofEpochSecond(oldestCall1Time / 1000, 0, ZoneOffset.UTC).toString(),
                logs.get(0).get("since"), "Avg time should be correct");
        assertEquals("call2", logs.get(1).get("name"), "Second call has lower avg run time");
    }
}