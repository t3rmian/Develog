package io.github.t3r1jj.develog.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity(name = "notes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Builder.Default
    private LocalDate date = LocalDate.now();
    @Builder.Default
    @Column(length = 65536)
    private String body = "";
    private boolean isGlobal;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "notes_tags", joinColumns = {
            @JoinColumn(name = "notes_id", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tags_value", referencedColumnName = "value", nullable = false),
            @JoinColumn(name = "tags_id", referencedColumnName = "userId", nullable = false)})
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public boolean addTag(Tag tag) {
        return tags.add(tag);
    }

    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
