package io.github.t3r1jj.develog.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
public class Note {
    @Id
    private Long id;
    private Instant creationTime;
    private Instant modifyTime;
    private String body;
    @ManyToMany
    private List<Tag> tags;
}
