package io.github.t3r1jj.develog.domain;

import lombok.Data;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Data
@Value
@Entity(name = "USERS")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    @OneToOne
    private Note globalNote;
    @OneToMany
    private List<Note> notes;
}
