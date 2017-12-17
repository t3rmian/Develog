package io.github.t3r1jj.develog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MonitoringDaoTest {

    @Mock
    private MongoTemplate mongoTemplate;
    private MonitoringDao monitoringDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        monitoringDao = new MonitoringDao(mongoTemplate);
    }

    @Test
    void getMongoDbSize_NoDB() {
        when(mongoTemplate.getDb()).thenThrow(new RuntimeException("No db"));
        assertEquals(-1, monitoringDao.getMongoDbSize());
    }
}