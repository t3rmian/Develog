package io.github.t3r1jj.develog.model.data;

import lombok.*;
import org.bson.types.ObjectId;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private Long id;
    @Setter
    private String name;
    @Setter
    private String email;
    @Builder.Default
    private Note globalNote = Note.builder().build();
    @Builder.Default
    private List<ObjectId> noteIds = new LinkedList<>();
    @Builder.Default
    @Setter
    private Role role = Role.USER;

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean infoEquals(User user) {
        return Objects.equals(this.email, user.email) &&
                Objects.equals(this.name, user.name);
    }

    public void addNote(ObjectId id) {
        noteIds.add(id);
    }

    public List<HashMap.Entry<ObjectId, LocalDate>> getNoteDates() {
        return noteIds.stream().map(it -> new HashMap.SimpleImmutableEntry<>(it, Note.idDateConverter.convert(it))).collect(Collectors.toList());
    }

    public enum Role {
        ADMIN, USER, BANNED
    }
}
