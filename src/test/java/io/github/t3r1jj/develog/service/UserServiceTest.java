package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private SessionService sessionService;
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(333L).build();
        MockitoAnnotations.initMocks(this);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User newUser = (User) invocationOnMock.getArguments()[0];
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(newUser));
            return newUser;
        });
        userService = spy(new UserService(sessionService, userRepository));
    }

    @Test
    void getUser() {
        assertEquals(user, userService.getUser(user.getId()).get());
    }

    @Test
    void updateUser() {
        Note newNote = Note.builder().id(new ObjectId()).build();
        String newEmail = "new@email.com";
        String newName = "newName";
        ArrayList<Note> newNotes = new ArrayList<Note>() {{
            this.add(Note.builder().id(new ObjectId()).build());
        }};
        User newUserData = User.builder()
                .id(user.getId())
                .globalNote(newNote)
                .email(newEmail)
                .name(newName)
                .noteIds(newNotes.stream().map(Note::getId).collect(Collectors.toList()))
                .build();
        user = userService.updateUser(newUserData);
        assertEquals(newUserData, user, "Same user by id");
        assertEquals(newUserData.getEmail(), user.getEmail(), "New notes");
        assertEquals(newUserData.getName(), user.getName(), "New name");
        assertEquals(newUserData.getGlobalNote(), user.getGlobalNote(), "New note");
        assertEquals(newUserData.getNoteIds(), user.getNoteIds(), "New notes");
    }

    @Test
    void registerUser() {
        userService.registerUser(user);
        Note newNote = Note.builder().id(new ObjectId()).build();
        String newEmail = "new@email.com";
        String newName = "newName";
        ArrayList<Note> newNotes = new ArrayList<Note>() {{
            this.add(Note.builder().id(new ObjectId()).build());
        }};
        User newUserData = User.builder()
                .id(user.getId())
                .globalNote(newNote)
                .email(newEmail)
                .name(newName)
                .noteIds(newNotes.stream().map(Note::getId).collect(Collectors.toList()))
                .build();
        user = userService.registerUser(newUserData);
        assertEquals(newUserData, user, "Same user by id");
        assertEquals(newUserData.getEmail(), user.getEmail(), "New notes");
        assertEquals(newUserData.getName(), user.getName(), "New name");
        assertNotEquals(newUserData.getGlobalNote(), user.getGlobalNote(), "Default global note");
        assertEquals(0, user.getNoteIds().size(), "Empty notes");
    }

    @Test
    void onAuthenticationSuccess() {
        User updatedUser = User.builder().id(333L).email("a").name("b").build();
        when(sessionService.getAuthenticatedUser()).thenReturn(updatedUser);
        userService.onAuthenticationSuccess();
        verify(userRepository).save(updatedUser);
    }

    @Test
    void changeRole() {
        assertTrue(userService.changeRole(user.getId(), User.Role.USER));
        assertEquals(User.Role.USER, user.getRole());
        assertTrue(userService.changeRole(user.getId(), User.Role.BANNED));
        assertEquals(User.Role.BANNED, user.getRole());
        assertTrue(userService.changeRole(user.getId(), User.Role.ADMIN));
        assertEquals(User.Role.ADMIN, user.getRole());
        assertFalse(userService.changeRole(27836127836L, User.Role.USER));
    }

    @Test
    void isUserAuthenticated() {
        userService.isUserAuthenticated();
        verify(sessionService).isSessionAuthenticated();
    }
}