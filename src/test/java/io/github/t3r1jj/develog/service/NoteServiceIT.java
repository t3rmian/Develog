package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.component.MongoConversions;
import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.NoteRepository;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NoteServiceIT {
    private static final MongoConversions.LocalDateToObjectIdConverter dateConverter = new MongoConversions.LocalDateToObjectIdConverter();
    private NoteService noteService;
    @Mock
    private SessionService sessionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteRepository noteRepository;
    private User user;


    private Note todayNote;
    private Note yesterdayNote;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(sessionService);
        todayNote = Note.builder()
                .id(dateConverter.convert(LocalDate.now()))
                .build();
        yesterdayNote = Note.builder()
                .id(dateConverter.convert(LocalDate.now().minusDays(1)))
                .build();
        user = User.builder()
                .id(123L)
                .globalNote(Note.builder()
                        .id(dateConverter.convert(LocalDate.now()))
                        .build()
                )
                .noteIds(new LinkedList<>(Arrays.asList(todayNote.getId(), yesterdayNote.getId())))
                .build();
        when(sessionService.getAuthenticatedUserId()).thenReturn(user.getId());
        when(sessionService.getAuthenticatedUser()).thenReturn(user);
        UserService userService = spy(new UserService(sessionService, userRepository));
        userRepository.save(user);
        noteService = new NoteService(userService, noteRepository);
        noteRepository.save(todayNote);
        noteRepository.save(yesterdayNote);
    }

    @Test
    void getAllTags() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        noteService.getNoteOrCreate(yesterday, user);
        noteService.getNoteOrCreate(today, user);

        noteService.addNoteTag(yesterday, "tag1");
        noteService.addNoteTag(yesterday, "tag2");
        noteService.addNoteTag(today, "tag2");
        noteService.addNoteTag(today, "tag3");
        assertEquals(3, noteService.getAllTags().size(), "Should return 3 tags for logged user");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getNote() {
        assertEquals(user.getGlobalNote(), noteService.getNote(null, user).get(), "Global note");
        assertEquals(todayNote, noteService.getNote(LocalDate.now(), user).get(),
                "Today's note");
        assertEquals(yesterdayNote, noteService.getNote(LocalDate.now().minusDays(1), user).get(),
                "Yesterday's note");
        assertFalse(noteService.getNote(LocalDate.now().plusYears(2), user).isPresent(), "Note not found");
    }

    @Test
    void getOrCreateNote() {
        Note newNote = noteService.getNoteOrCreate(LocalDate.now().plusYears(2), user);
        assertNotNull(newNote, "New note created");
        assertEquals(newNote, noteService.getNoteOrCreate(LocalDate.now().plusYears(2), user), "Returned cached note");
        assertTrue(noteService.getAllNotes(user).contains(newNote), "And the note has been put in user's collection");
    }

    @Test
    void getAllNotes() {
        assertEquals(3, noteService.getAllNotes(user).size());
        assertEquals(2, noteService.getAllNotes(user).stream().filter(it -> it.getDate().isEqual(LocalDate.now())).count(),
                "We created note for today and another one is global");
        assertTrue(noteService.getAllNotes(user).contains(yesterdayNote));
    }


}
