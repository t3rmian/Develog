package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(333L).build();
        MockitoAnnotations.initMocks(this);
        when(userRepository.getOne(user.getId())).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User newUser = (User) invocationOnMock.getArguments()[0];
            when(userRepository.getOne(user.getId())).thenReturn(newUser);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(newUser));
            return newUser;
        });
        userService = spy(new UserService(userRepository));
    }

    @Test
    void getUser() {
        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    void updateUser() {
        Note newNote = Note.builder().id(3L).build();
        String newEmail = "new@email.com";
        String newName = "newName";
        ArrayList<Note> newNotes = new ArrayList<Note>() {{
            this.add(Note.builder().id(4L).build());
        }};
        User newUserData = User.builder()
                .id(user.getId())
                .globalNote(newNote)
                .email(newEmail)
                .name(newName)
                .notes(newNotes)
                .build();
        user = userService.updateUser(newUserData);
        assertEquals(newUserData, user, "Same user by id");
        assertEquals(newUserData.getEmail(), user.getEmail(), "New notes");
        assertEquals(newUserData.getName(), user.getName(), "New name");
        assertEquals(newUserData.getGlobalNote(), user.getGlobalNote(), "New note");
        assertEquals(newUserData.getNotes(), user.getNotes(), "New notes");
    }

    @Test
    void registerUser() {
        userService.registerUser(user);
        Note newNote = Note.builder().id(3L).build();
        String newEmail = "new@email.com";
        String newName = "newName";
        ArrayList<Note> newNotes = new ArrayList<Note>() {{
            this.add(Note.builder().id(4L).build());
        }};
        User newUserData = User.builder()
                .id(user.getId())
                .globalNote(newNote)
                .email(newEmail)
                .name(newName)
                .notes(newNotes)
                .build();
        user = userService.registerUser(newUserData);
        assertEquals(newUserData, user, "Same user by id");
        assertEquals(newUserData.getEmail(), user.getEmail(), "New notes");
        assertEquals(newUserData.getName(), user.getName(), "New name");
        assertNotEquals(newUserData.getGlobalNote(), user.getGlobalNote(), "Default global note");
        assertEquals(0, user.getNotes().size(), "Empty notes");
    }


}