package io.github.t3r1jj.develog.model.monitor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    @Test
    void getDate() {
        assertEquals(LocalDateTime.ofEpochSecond(new Date().getTime() / 1000, 0, ZoneOffset.UTC),
                new Error("test", "null", "info").getDate());
    }
}