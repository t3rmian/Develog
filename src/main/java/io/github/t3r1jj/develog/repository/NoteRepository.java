package io.github.t3r1jj.develog.repository;

import io.github.t3r1jj.develog.domain.Note;
import org.springframework.data.repository.CrudRepository;

public interface NoteRepository extends CrudRepository<Note, Long> {
}
