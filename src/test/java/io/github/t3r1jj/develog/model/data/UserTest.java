package io.github.t3r1jj.develog.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {


    private Note todayNote;
    private Note yesterdayNote;
    private User user;

    @BeforeEach
    void setUp() {
        todayNote = Note.builder()
                .id((long) 1)
                .date(LocalDate.now())
                .build();
        yesterdayNote = Note.builder()
                .id((long) 2)
                .date(LocalDate.now().minusDays(1))
                .build();
        user = User.builder()
                .globalNote(Note.builder()
                        .id((long) 0)
                        .build()
                )
                .notes(new HashSet<Note>() {{
                    this.add(todayNote);
                    this.add(yesterdayNote);
                }})
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getNote() {
        assertEquals(user.getGlobalNote(), user.getNote(null).get(), "Global note");
        assertEquals(todayNote, user.getNote(LocalDate.now()).get(),
                "Today's note");
        assertEquals(yesterdayNote, user.getNote(LocalDate.now().minusDays(1)).get(),
                "Yesterday's note");
        assertFalse(user.getNote(LocalDate.now().plusYears(2)).isPresent(), "Note not found");
    }

    @Test
    void getOrCreateNote() {
        Note newNote = user.getOrCreate(LocalDate.now().plusYears(2));
        assertTrue(newNote != null, "New note created");
        assertEquals(newNote, user.getOrCreate(LocalDate.now().plusYears(2)), "Returned cached note");
        assertTrue(user.getNotes().contains(newNote), "And the note has been put in user's collection");
    }

}