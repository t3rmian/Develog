package io.github.t3r1jj.develog.domain;

import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Value
@Builder
@Entity(name = "USERS")
public class User {
    @Id
    String id;
    String name;
    String email;
    @OneToOne
    Note globalNote;
    @OneToMany
    List<Note> notes;
}
