package io.github.t3r1jj.develog.model.data;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private Note globalNote;
    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<Note> notes = Collections.emptyList();

    public Optional<Note> getNote(@Nullable LocalDate date) {
        if (date == null) {
            return Optional.of(globalNote);
        }
        return notes.stream().filter(note -> note.getDate().isEqual(date)).findAny();
    }

    public Note getOrCreate(@Nullable LocalDate date) {
        return getNote(date).orElseGet(() -> {
            Note note = Note.builder().date(date).build();
            notes.add(note);
            return note;
        });
    }

    public List<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<>(this.notes);
        notes.add(globalNote);
        return notes;
    }

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
}
