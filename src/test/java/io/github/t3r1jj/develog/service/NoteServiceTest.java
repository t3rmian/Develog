package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.NoteRepository;
import io.github.t3r1jj.develog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class NoteServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private NoteRepository noteRepository;
    private NoteService noteService;
    private Note note;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        user = spy(User.builder().id(111L).build());
        note = Note.builder().id(222L).build();
        when(user.getNote(LocalDate.now())).thenReturn(Optional.of(note));
        when(userService.getLoggedUser()).thenReturn(user);
        noteService = new NoteService(userService, noteRepository, tagRepository);
    }

    @Test
    void getNote() {
        assertEquals(note, noteService.getNote(LocalDate.now()).get());
    }

    @Test
    void updateNoteBody() {
        noteService.updateNoteBody(LocalDate.now(), "test2");
        assertEquals("test2", note.getBody());
    }

    @Test
    void addNoteTag() {
        assertTrue(noteService.addNoteTag(LocalDate.now(), "tag2"));
        assertEquals(1, note.getTags().size(), "Should contain one tag");
        Tag tag = note.getTags().iterator().next();
        assertEquals("tag2", tag.getId().getValue(), "Tag value should match");
        assertEquals(user.getId(), tag.getId().getUserId(), "Tag should be associated with correct user");
    }

    @Test
    void addNoteTagDuplicate() {
        assertTrue(noteService.addNoteTag(LocalDate.now(), "tag2"));
        assertFalse(noteService.addNoteTag(LocalDate.now(), "tag2"));
    }

    @Test
    void removeNoteTag() {
        note.getTags().add(new Tag("tag3", user.getId()));
        assertTrue(noteService.removeNoteTag(LocalDate.now(), "tag3"));
        assertEquals(0, note.getTags().size(), "Should not contain any tags");
    }

    @Test
    void removeNoteTagNone() {
        assertFalse(noteService.removeNoteTag(LocalDate.now(), "tag3"));
    }

}