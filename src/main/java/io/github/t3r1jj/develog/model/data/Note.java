package io.github.t3r1jj.develog.model.data;

import io.github.t3r1jj.develog.component.MongoConversions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Document(collection = "notes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    static final MongoConversions.ObjectIdToLocalDateConverter idDateConverter = new MongoConversions.ObjectIdToLocalDateConverter();
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    @Builder.Default
    private String body = "";
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    public boolean addTag(String tag) {
        return tags.add(tag);
    }

    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        return id.equals(((Note) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public LocalDate getDate() {
        return idDateConverter.convert(id);
    }

}
