package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.NoteRepository;
import io.github.t3r1jj.develog.repository.data.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Transactional(readOnly = true)
    public Optional<Note> getNote(LocalDate date) {
        User loggedUser = userService.getLoggedUser();
        return loggedUser.getNote(date);
    }

    @Transactional
    public void updateNoteBody(LocalDate date, String body) {
        User loggedUser = userService.getLoggedUser();
        Note note = loggedUser.getNote(date).get();
        note.setBody(body);
        updateNote(note);
    }

    @Transactional
    public boolean addNoteTag(LocalDate date, String tag) {
        User loggedUser = userService.getLoggedUser();
        Note note = loggedUser.getNote(date).get();
        if (note.addTag(new Tag(tag, loggedUser.getId()))) {
            updateNote(note);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean removeNoteTag(LocalDate date, String tag) {
        User loggedUser = userService.getLoggedUser();
        Note note = loggedUser.getNote(date).get();
        if (note.removeTag(new Tag(tag, loggedUser.getId()))) {
            updateNote(note);
            return true;
        }
        return false;
    }

    @Transactional
    private void updateNote(Note note) {
        persistNewTags(note);
        noteRepository.saveAndFlush(note);
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public List<Note> findAllByTags(List<String> values) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        User loggedUser = userService.getLoggedUser();
        List<Tag> tags = values.stream().map(v -> new Tag(v, loggedUser.getId())).collect(Collectors.toList());
        System.out.println("HMMM " + loggedUser.getAllNotes());
        return loggedUser.getAllNotes().stream()
                .peek(Note::getTags) // TODO: LAZY FETCH
                .filter(n -> tagsMatch(n.getTags(), tags))
                .collect(Collectors.toList());
    }

    private boolean tagsMatch(Set<Tag> t1, List<Tag> t2) {
        for (Tag t : t2) {
            if (!t1.contains(t)) {
                return false;
            }
        }
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<Note> findByDate(LocalDate date) {
        User loggedUser = userService.getLoggedUser();
        return loggedUser.getNote(date);
    }
}
