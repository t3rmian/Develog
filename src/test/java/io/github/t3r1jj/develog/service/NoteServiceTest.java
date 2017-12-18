package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.NoteRepository;
import io.github.t3r1jj.develog.repository.data.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void findAllByTags() {
        Tag tag1 = new Tag("tag1", user.getId());
        Note note2 = Note.builder().id(3L).build();
        Note note3 = Note.builder().id(4L).build();
        when(user.getAllNotes()).thenReturn(Arrays.asList(note, note2, note3));
        note.setTags(new HashSet<Tag>() {{
            this.add(tag1);
        }});
        note2.setTags(new HashSet<Tag>() {{
            this.add(tag1);
        }});
        assertTrue(noteService.findAllByTags(Collections.singletonList("tag1")).contains(note));
        assertTrue(noteService.findAllByTags(Collections.singletonList("tag1")).contains(note2));
        assertFalse(noteService.findAllByTags(Collections.singletonList("tag1")).contains(note3));
        assertFalse(noteService.findAllByTags(Arrays.asList("tag1", "tag2")).contains(note));
    }

    @Test
    void findAllByTags_Asterisk() {
        Note note2 = Note.builder().id(3L).build();
        Note note3 = Note.builder().id(4L).build();
        when(user.getAllNotes()).thenReturn(Arrays.asList(note, note2, note3));
        assertTrue(noteService.findAllByTags(Collections.singletonList("*")).contains(note));
        assertTrue(noteService.findAllByTags(Collections.singletonList("*")).contains(note2));
        assertTrue(noteService.findAllByTags(Collections.singletonList("*")).contains(note3));
    }

    @Test
    void findAllByTags_Empty() {
        assertEquals(0, noteService.findAllByTags(Collections.emptyList()).size());
    }

    @Test
    void findByDate() {
        assertEquals(note, noteService.findByDate(LocalDate.now()).get());
    }

    @Test
    void getNoteOrCreate() {
        noteService.getNoteOrCreate(LocalDate.now());
        verify(userService).updateUser(user);
        verify(user).getNoteOrCreate(LocalDate.now());
    }

    @Test
    void getNoteDates() {
        noteService.getNoteDates();
        verify(userService).getUserNoteDates();
    }
}