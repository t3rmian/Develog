package io.github.t3r1jj.develog.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Tag {
    @Id
    private Long id;
    private String value;
}
