package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.monitor.Event;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MonitoringDaoTestIT {

    @Autowired
    private MonitoringDao monitoringDao;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CallRepository callRepository;
    @Autowired
    private ErrorRepository errorRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        callRepository.deleteAll();
        errorRepository.deleteAll();
    }

    @Test
    void truncateEvents() throws InterruptedException {
        Event event = new Event();
        Thread.sleep(10);
        eventRepository.saveAll(Arrays.asList(event, new Event(), new Event(), new Event(), new Event()));
        assertEquals(5, eventRepository.findAll().size(), "Initializing event repository with data");
        monitoringDao.truncateEvents(1);
        assertEquals(1, eventRepository.findAll().size(), "Truncated");
        assertEquals(event.getTime(), eventRepository.findAll().get(0).getTime(), "Truncated to newest");
    }

    @Test
    void eventRepository_findAllByOrderByTimeAsc() throws InterruptedException {
        Event event = new Event();
        Thread.sleep(10);
        eventRepository.saveAll(Arrays.asList(event, new Event(), new Event(), new Event(), new Event()));
        assertEquals(event.getTime(), eventRepository.findAll().get(0).getTime(), "ASC by time - first is the oldest");
    }

    @Test
    void getMongoDbSize() {
        assertEquals(0, monitoringDao.getMongoDbSize(), "0MB db size");
    }
}