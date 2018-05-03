package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceIT {

    @Mock
    private SessionService sessionService;
    @Autowired
    private UserRepository userRepository;
    private UserService userService;
    private User user;
    private User userWithNoEmail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(sessionService, userRepository);
        user = User.builder()
                .globalNote(Note.builder().build())
                .noteIds(new ArrayList<>())
                .email("abc@abc.abc")
                .id(123L).build();
        userWithNoEmail = User.builder()
                .globalNote(Note.builder().build())
                .noteIds(new ArrayList<>())
                .id(123L).build();
        userService.registerUser(user);
    }

    @Test
    void registerUser() {
        User dbUser = userService.getUser(user.getId()).get();
        assertEquals(user, dbUser, "User registered in db");
        assertNotNull(dbUser.getGlobalNote().getId(), "Initialized global note");
        assertEquals(0, dbUser.getNoteIds().size(), "Other notes are empty");
    }

    @Test
    void getEmails() {
        assertTrue(userService.getUserEmails().contains(user.getEmail()));
        assertFalse(userService.getUserEmails().contains(userWithNoEmail.getEmail()));
    }

}
