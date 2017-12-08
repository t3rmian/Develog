package io.github.t3r1jj.develog.model.data;

import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Value
@Entity(name = "tags")
public class Tag {
    @Id
    @GeneratedValue
    private long id;
    private String value;
}
