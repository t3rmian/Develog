package io.github.t3r1jj.develog.component;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateConverterTest {

    @Test
    void convert() {
        LocalDateConverter localDateConverter = new LocalDateConverter();
        String date = "2017-12-14";
        assertEquals(LocalDate.of(2017, 12, 14), localDateConverter.convert(date));
    }
}