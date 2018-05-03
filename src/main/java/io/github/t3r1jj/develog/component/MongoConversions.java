package io.github.t3r1jj.develog.component;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class MongoConversions {

    @Component
    @WritingConverter
    public static final class LocalDateToObjectIdConverter implements Converter<LocalDate, ObjectId> {

        @Override
        public ObjectId convert(LocalDate date) {
            return new ObjectId(Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)));
        }
    }

    @Component
    @ReadingConverter
    public static final class ObjectIdToLocalDateConverter implements Converter<ObjectId, LocalDate> {

        @NotNull
        @Override
        public LocalDate convert(ObjectId objectId) {
            return objectId.getDate().toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
        }
    }
}
