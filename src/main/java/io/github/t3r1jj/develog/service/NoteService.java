package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.repository.NoteRepository;
import io.github.t3r1jj.develog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final UserService userService;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    @Autowired
    public NoteService(UserService userService, NoteRepository noteRepository, TagRepository tagRepository) {
        this.userService = userService;
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    public void updateNote(Note note) {
        noteRepository.save(note);
    }

}
