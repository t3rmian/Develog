package io.github.t3r1jj.develog.model.data;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void addTag() {
        Note note = Note.builder().build();
        assertEquals(0, note.getTags().size(), "Tags are ampty");
        assertTrue(note.addTag(new Tag("tag2")), "Successful insert");
        assertEquals(1, note.getTags().size(), "Added one tag");
        assertFalse(note.addTag(new Tag("tag2")), "Same tag not added");
        assertEquals(1, note.getTags().size(), "Still one tag");
    }

    @Test
    void removeTag() {
        Note note = Note.builder().tags(new HashSet<Tag>() {{
            this.add(new Tag("tag1"));
            this.add(new Tag("tag2"));
        }}).build();
        assertEquals(2, note.getTags().size(), "Two tags present");
        assertTrue(note.removeTag(new Tag("tag2")), "Successful removal");
        assertEquals(1, note.getTags().size(), "Removed one of two tags");
        assertFalse(note.removeTag(new Tag("tag2")), "Cannot remove not existent tag");
        assertEquals(1, note.getTags().size(), "Still one tag");
    }

}