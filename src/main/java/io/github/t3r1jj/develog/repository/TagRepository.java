package io.github.t3r1jj.develog.repository;

import io.github.t3r1jj.develog.domain.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Long> {
}
