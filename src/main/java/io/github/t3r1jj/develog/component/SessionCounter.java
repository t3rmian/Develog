package io.github.t3r1jj.develog.component;

import io.github.t3r1jj.develog.model.monitor.Event;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import io.github.t3r1jj.develog.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;
import java.util.Set;

@Component
public class SessionCounter implements HttpSessionListener {
    private Set<String> sessions = new HashSet<>();
    private final EventRepository eventRepository;
    private final MonitoringService monitoringService;

    @Autowired
    public SessionCounter(EventRepository eventRepository, MonitoringService monitoringService) {
        this.eventRepository = eventRepository;
        this.monitoringService = monitoringService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.add(session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.remove(session.getId());
    }

    public int getActiveSessionNumber() {
        return sessions.size();
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void dumpToDb() {
        eventRepository.save(new Event(Event.Type.ONLINE, getActiveSessionNumber()));
        monitoringService.truncateEvents(1000);
    }
}
