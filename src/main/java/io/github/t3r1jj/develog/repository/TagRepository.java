package io.github.t3r1jj.develog.repository;

import io.github.t3r1jj.develog.model.data.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {
}
