package io.github.t3r1jj.develog.repository.data;

import io.github.t3r1jj.develog.model.data.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Tag.TagId> {
    @Query("SELECT t.id.value FROM tags t WHERE t.id.userId = :id")
    List<String> findAllTagValuesByUserId(@Param("id") Long id);
}
