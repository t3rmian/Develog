package io.github.t3r1jj.develog.component;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@ReadingConverter
@WritingConverter
public final class LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(@NonNull String source) {
        return LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
