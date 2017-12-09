package io.github.t3r1jj.develog.repository;

import io.github.t3r1jj.develog.model.data.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
