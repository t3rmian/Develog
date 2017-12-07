package io.github.t3r1jj.develog.domain;

import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.Id;

@Value
@Entity
public class Tag {
    @Id
    private Long id;
    private String value;
}
