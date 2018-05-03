package io.github.t3r1jj.develog.model.data;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void addTag() {
        Note note = Note.builder().build();
        assertEquals(0, note.getTags().size(), "Tags are ampty");
        assertTrue(note.addTag(createTag("tag2")), "Successful insert");
        assertEquals(1, note.getTags().size(), "Added one tag");
        assertFalse(note.addTag(createTag("tag2")), "Same tag not added");
        assertEquals(1, note.getTags().size(), "Still one tag");
    }

    @Test
    void removeTag() {
        Note note = Note.builder().tags(new HashSet<String>() {{
            this.add(createTag("tag1"));
            this.add(createTag("tag2"));
        }}).build();
        assertEquals(2, note.getTags().size(), "Two tags present");
        assertTrue(note.removeTag(createTag("tag2")), "Successful removal");
        assertEquals(1, note.getTags().size(), "Removed one of two tags");
        assertFalse(note.removeTag(createTag("tag2")), "Cannot remove not existent tag");
        assertEquals(1, note.getTags().size(), "Still one tag");
    }

    private String createTag(String value) {
        return value;
    }

    @Test
    void testHashCode() {
        ObjectId id = new ObjectId();
        assertNotEquals(Note.builder().build().hashCode(), Note.builder().build().hashCode());
        assertEquals(Note.builder().id(id).build().hashCode(), Note.builder().id(id).build().hashCode());
        assertNotEquals(Note.builder().id(id).build().hashCode(), Note.builder().id(new ObjectId()).build().hashCode());
    }
}