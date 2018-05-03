package io.github.t3r1jj.develog.component;

import io.github.t3r1jj.develog.model.monitor.Event;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import io.github.t3r1jj.develog.service.MonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionCounterTest {

    private SessionCounter sessionCounter;
    @Mock
    private HttpSessionEvent sessionEvent;
    @Mock
    private HttpSessionEvent sessionEvent2;
    @Mock
    private HttpSession session;
    @Mock
    private HttpSession session2;
    @Mock
    private MonitoringService monitoringService;
    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(sessionEvent.getSession()).thenReturn(session);
        when(sessionEvent2.getSession()).thenReturn(session2);
        when(session.getId()).thenReturn("1");
        when(session2.getId()).thenReturn("2");
        sessionCounter = new SessionCounter(eventRepository, monitoringService);
    }

    @Test
    void sessionCreated() {
        sessionCounter.sessionCreated(sessionEvent);
        assertEquals(1, sessionCounter.getActiveSessionNumber());
        sessionCounter.sessionCreated(sessionEvent);
        assertEquals(1, sessionCounter.getActiveSessionNumber());
        sessionCounter.sessionCreated(sessionEvent2);
        assertEquals(2, sessionCounter.getActiveSessionNumber());
    }

    @Test
    void sessionCreatedDestroyed() {
        sessionCounter.sessionDestroyed(sessionEvent);
        assertEquals(0, sessionCounter.getActiveSessionNumber());
        sessionCounter.sessionCreated(sessionEvent);
        sessionCounter.sessionCreated(sessionEvent2);
        sessionCounter.sessionDestroyed(sessionEvent);
        assertEquals(1, sessionCounter.getActiveSessionNumber());
        sessionCounter.sessionDestroyed(sessionEvent2);
        assertEquals(0, sessionCounter.getActiveSessionNumber());
    }

    @Test
    void dumpToDb() {
        sessionCounter.dumpToDb();
        verify(eventRepository).save(any(Event.class));
        verify(monitoringService).truncateEvents(anyInt());
    }
}