package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.Application;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.monitor.Error;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class ExceptionLoggingAspectIT {

    @Autowired
    private ErrorRepository errorRepository;
    @Autowired
    private UserService userService;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        errorRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        errorRepository.deleteAll();
    }

    @Test
    void logAfterError() {
        long time = new Date().getTime();
        when(user.getId()).thenThrow(new RuntimeException("rex"));
        try {
            userService.registerUser(user);
        } catch (Throwable ex) {
            List<Error> logs = errorRepository.findAll();
            assertEquals(1, logs.size(), "One log in db");
            assertTrue(logs.get(0).getName().contains("registerUser"), "Contains call name");
            assertTrue(logs.get(0).getStackTrace() != null && !logs.get(0).getStackTrace().isEmpty(), "Has a stacktrace");
            System.out.println(logs.get(0).getTime());
            assertTrue(logs.get(0).getTime() - time >= 0, "The time is correct");
            return;
        }
        fail("Mocked service should have thrown an exception");
    }


}