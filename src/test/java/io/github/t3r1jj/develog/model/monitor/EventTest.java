package io.github.t3r1jj.develog.model.monitor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void getDate() {
        Event test = new Event("test", null, null);
        assertEquals(LocalDateTime.ofEpochSecond(new Date().getTime() / 1000, 0, ZoneOffset.UTC),
                test.getDate());
    }
}