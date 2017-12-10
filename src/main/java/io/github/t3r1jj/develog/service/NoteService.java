package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.NoteRepository;
import io.github.t3r1jj.develog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<Note> getNote(LocalDate date) {
        User loggedUser = userService.getLoggedUser();
        return loggedUser.getNote(date);
    }

    public void updateNoteBody(LocalDate date, String body) {
        User loggedUser = userService.getLoggedUser();
        Note note = loggedUser.getNote(date).get();
        note.setBody(body);
        updateNote(note);
    }

    public boolean addNoteTag(LocalDate date, String tag) {
        User loggedUser = userService.getLoggedUser();
        Note note = loggedUser.getNote(date).get();
        if (note.addTag(new Tag(tag, loggedUser.getId()))) {
            updateNote(note);
            return true;
        }
        return false;
    }

    public boolean removeNoteTag(LocalDate date, String tag) {
        User loggedUser = userService.getLoggedUser();
        Note note = loggedUser.getNote(date).get();
        if (note.removeTag(new Tag(tag, loggedUser.getId()))) {
            updateNote(note);
            return true;
        }
        return false;
    }

    private void updateNote(Note note) {
        persistNewTags(note);
        noteRepository.saveAndFlush(note);
    }

    private void persistNewTags(Note note) {
        List<Tag> presentTags = tagRepository.findAllById(note.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet())
        );
        tagRepository.saveAll(note.getTags().stream()
                .filter(tag -> !presentTags.contains(tag))
                .collect(Collectors.toSet())
        );
    }

}
