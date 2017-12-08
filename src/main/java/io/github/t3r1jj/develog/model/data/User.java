package io.github.t3r1jj.develog.model.data;

import lombok.Builder;
import lombok.Value;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Value
@Builder
@Entity(name = "users")
public class User {
    @Id
    String id;
    String name;
    String email;
    @OneToOne
    transient Note globalNote = Note.builder()
            .isGlobal(true)
            .build();
    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    List<Note> notes = Collections.emptyList();
}
