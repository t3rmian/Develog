package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.NoteRepository;
import io.github.t3r1jj.develog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class NoteServiceIT {
    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private NoteRepository noteRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id("abc").build();
        user = userService.registerUser(user);
    }

    @Test
    @Transactional
    void updateGlobalNote() {
        Note note = user.getGlobalNote();
        note.setBody("test");
        noteService.updateNote(note);
        user = userService.getUser(user.getId());
        assertEquals(note, user.getGlobalNote(), "Same notes");
        String expectedText = "test2";
        user.getGlobalNote().setBody(expectedText);
        noteService.updateNote(note);
        assertEquals(note, user.getGlobalNote(), "Same notes after second update");
        assertEquals(expectedText, user.getGlobalNote().getBody(), "Expected body text");
    }

    @Test
    @Transactional
    void updateNoteTags() {
        Note globalNote = user.getGlobalNote();
        noteService.updateNote(globalNote);
        globalNote.getTags().add(new Tag("tag1"));
        globalNote.getTags().add(new Tag("tag2"));
        noteService.updateNote(globalNote);
        System.out.println(globalNote);
        globalNote.getTags().add(new Tag("tag3"));
        globalNote.getTags().add(new Tag("tag4"));
        noteService.updateNote(globalNote);
        System.out.println(globalNote);
        globalNote.getTags().add(new Tag("tag5"));
        globalNote.getTags().add(new Tag("tag6"));
        noteService.updateNote(globalNote);

        user = userService.getUser(user.getId());
        assertEquals(6, user.getGlobalNote().getTags().size(), "Not should now have 6 tags");
    }

    @Test
    @Transactional
    @DisplayName("Db should contain 3 different tags (1 shared) for 2 notes")
    void multipleNotesMultipleTags() {
        Note globalNote = user.getGlobalNote();
        globalNote.getTags().add(new Tag("tag1"));
        globalNote.getTags().add(new Tag("tag2"));
        noteService.updateNote(globalNote);
        Note newNote = Note.builder().tags(new HashSet<Tag>() {{
            this.add(new Tag("tag2"));
            this.add(new Tag("tag3"));
        }}).build();
        noteService.updateNote(newNote);
        assertEquals(3, tagRepository.findAll().size(), "DB should have 3 different tags");
        newNote = noteRepository.getOne(newNote.getId());
        assertEquals(2, newNote.getTags().size(), "New note persisted with 2 tags");
    }
}
