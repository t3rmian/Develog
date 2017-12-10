package io.github.t3r1jj.develog.model.data;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity(name = "tags")
@NoArgsConstructor
public class Tag {
    @EmbeddedId
    private TagId id;

    public Tag(String value, Long userId) {
        this.id = new TagId(value, userId);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Getter
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class TagId implements Serializable {
        private String value;
        private Long userId;

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
            TagId tagId = (TagId) o;
            return Objects.equals(value, tagId.value) && Objects.equals(tagId.userId, tagId.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, userId);
        }
    }

}
