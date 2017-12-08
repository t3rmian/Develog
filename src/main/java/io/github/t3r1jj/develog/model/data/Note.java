package io.github.t3r1jj.develog.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "notes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    @GeneratedValue
    private long id;
    @Builder.Default
    private Instant creationTime = Instant.now();
    @Builder.Default
    private Instant modifyTime = Instant.now();
    private String body;
    private boolean isGlobal;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(name = "notes_tags", joinColumns = {
            @JoinColumn(name = "notes", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tags", referencedColumnName = "id", nullable = false)})
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

}
