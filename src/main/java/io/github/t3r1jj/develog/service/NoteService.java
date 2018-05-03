package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.component.MongoConversions;
import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.xml.ws.http.HTTPException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NoteService {
    private static final MongoConversions.LocalDateToObjectIdConverter dateConverter = new MongoConversions.LocalDateToObjectIdConverter();
    private final UserService userService;
    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(UserService userService, NoteRepository noteRepository) {
        this.userService = userService;
        this.noteRepository = noteRepository;
    }


    public Optional<Note> getNote(LocalDate date) {
        User loggedUser = userService.getLoggedUser();
        return getNote(date, loggedUser);
    }


    public void updateNoteBody(LocalDate date, String body) {
        User loggedUser = userService.getLoggedUser();
        Note note = getNote(date, loggedUser).orElseThrow(() -> new HTTPException(404));
        note.setBody(body);
        updateNote(loggedUser, note);
    }

    private void updateNote(User user, Note note) {
        if (user.getGlobalNote().equals(note)) {
            userService.updateUser(user);
        } else {
            noteRepository.save(note);
        }
    }


    public boolean addNoteTag(LocalDate date, String tag) {
        User loggedUser = userService.getLoggedUser();
        Note note = getNote(date, loggedUser).orElseThrow(() -> new HTTPException(404));
        if (note.addTag(tag)) {
            updateNote(loggedUser, note);
            return true;
        }
        return false;
    }


    public boolean removeNoteTag(LocalDate date, String tag) {
        User loggedUser = userService.getLoggedUser();
        Note note = getNote(date, loggedUser).orElseThrow(() -> new HTTPException(404));
        if (note.removeTag(tag)) {
            updateNote(loggedUser, note);
            return true;
        }
        return false;
    }

    public List<Note> findAllByTags(List<String> values) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        } else if (values.contains("*")) {
            return getAllNotes(userService.getLoggedUser());
        }
        User loggedUser = userService.getLoggedUser();
        return getAllNotes(loggedUser).stream()
                .filter(n -> tagsMatch(n.getTags(), values))
                .collect(Collectors.toList());
    }

    private boolean tagsMatch(Set<String> t1, List<String> t2) {
        for (String t : t2) {
            if (!t1.contains(t)) {
                return false;
            }
        }
        return true;
    }


    public Optional<Note> findByDate(LocalDate date) {
        User loggedUser = userService.getLoggedUser();
        return getNote(date, loggedUser);
    }


    public Note getNoteOrCreate(LocalDate date) {
        User loggedUser = userService.getLoggedUser();
        Note note = getNoteOrCreate(date, loggedUser);
        userService.updateUser(loggedUser);
        return note;
    }

    public Optional<Note> getNote(@Nullable LocalDate date, User user) {
        if (date == null) {
            return Optional.of(user.getGlobalNote());
        }
        return user.getNoteDates().stream()
                .filter(note -> note.getValue().isEqual(date))
                .findAny().flatMap(it -> noteRepository.findById(it.getKey()));
    }

    public Note getNoteOrCreate(@Nullable LocalDate date, User user) {
        return getNote(date, user).orElseGet(() -> {
            Note note = Note.builder().id(dateConverter.convert(date)).build();
            noteRepository.save(note);
            user.addNote(note.getId());
            return note;
        });
    }

    public List<Note> getAllNotes(User user) {
        ArrayList<Note> notes = getDailyNotes(user);
        notes.add(user.getGlobalNote());
        return notes;
    }

    public ArrayList<Note> getDailyNotes(User user) {
        return StreamSupport.stream(noteRepository.findAllById(user.getNoteIds()).spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LocalDate> getNoteDates() {
        return userService.getUserNoteDates();
    }


    public Set<String> getAllTags() {
        User loggedUser = userService.getLoggedUser();
        HashSet<String> dailyTags = noteRepository.findAllProjectedTagsById(loggedUser.getNoteIds())
                .stream()
                .flatMap(it -> it.getTags().stream())
                .collect(Collectors.toCollection(HashSet::new));
        dailyTags.addAll(loggedUser.getGlobalNote().getTags());
        return dailyTags;
    }

}
