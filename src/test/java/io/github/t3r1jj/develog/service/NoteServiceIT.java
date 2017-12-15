package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.NoteRepository;
import io.github.t3r1jj.develog.repository.data.TagRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NoteServiceIT {
    private NoteService noteService;
    @Mock
    private SessionService sessionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private NoteRepository noteRepository;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(sessionService);
        user = User.builder().id(123L).build();
        when(sessionService.getAuthenticatedUserId()).thenReturn(user.getId());
        when(sessionService.getAuthenticatedUser()).thenReturn(user);
        UserService userService = spy(new UserService(sessionService, userRepository));
        user = userService.registerUser(user);
        noteService = new NoteService(userService, noteRepository, tagRepository);
    }

    @Test
    @Transactional
    @DisplayName("Db should contain 3 different tags (1 shared) for 2 user notes")
    void addNoteTag() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        user.getNoteOrCreate(yesterday);
        user.getNoteOrCreate(today);
        user = userRepository.save(user);

        noteService.addNoteTag(yesterday, "tag1");
        noteService.addNoteTag(yesterday, "tag2");
        noteService.addNoteTag(today, "tag2");
        noteService.addNoteTag(today, "tag3");

        assertEquals(3, tagRepository.findAll().size(), "DB should have 3 different tags");
    }
}
