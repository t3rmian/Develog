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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private static final String STRING_1kB = "vbaYJUHZr2T7il5lekNOfmUELm4oCmTiKQC6QQPM5NRLBGsVgJ01QlTsIllwSp91Yj41jCXBUzDapzRG80BK4hwuyVmkRB6fgOBWwQFcHU80ejAKMZYtCN978AEwI677cimIyIElq7nUIGm0S05X7nF9rBmsoDb08FMTZE5LJ8v5J6KWrQKEeOfynOru2jUlONTY1Ze65NX0h3UvrbRTWwfoF03HURbRjsDeVw3eiJoB9Xk7FEecNKSeOEGO3FtZjZZ3HbuhmmwAO6D2Y20XP3GHA8Qygr3rFzES9TwaBt0Xpt5B8loFpnEjSpI2JiVKVWnqL8tZyJLnSbWppvvZFsZUAXvyivxGYFQSor6WAwvqqmXGekLyAjiL3YVDHTs2jrmcUEm6D8Z5iM9lEVV5Ve0kInRt0u97xven1hh0L87xS1xEutfe6vFibHDAVDkeElNLHQbNXu2vefX8XnFWE7iKKR34xYySa5T5WFEg69Ety2jFlF91KyUPsFPHoKKnAFVLjPgu0yKchZvuCwkDvevAbtjiCDHaNzwKg6CncR2oV4F7yW2cIuBhkkbXGEnY7gEOTEUqKegz4bYFQLk8NDqXDHLKACuOOWcrKENjJxRrotIhEqTwBhmJlepPErUX6OZLTVEHyErTEGMCfnpENE8Pikncytlkg1Z9Tg7Xn6axcm21HUjZ7b6R928tsyLlViruonVZKqNlbRlRFb3LzPCH3DEmTKpNHpYW54rxpIJmnEKtLkvTcVrT8RWDsQhFzyQKD7mLkRb067Ji7xzYys9cWGcKBqFGxt7NNGaGODRpMSreeSr3nsNHLBq2HV1VvFNtwx6tCpEcYiGOccQCoe7zimDOU4nbPbmjhqegnfP6DJhvKtFJCUpc5iwC716JD0h4A6S1Oq3z23Wrc6qQX3s8fioe6JgU7XiHYknTeqx1SvY2kzGmlAsc9TREvAYaPrcGzCjyBsvCjlhHHQwLBvi13J5tpI0TlPp58Bli";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(sessionService, userRepository);
        user = User.builder()
                .globalNote(Note.builder().build())
                .notes(new ArrayList<>())
                .id(123L).build();
        userService.registerUser(user);
    }

    @Test
    @Transactional
    void registerUser() {
        User dbUser = userRepository.getOne(user.getId());
        assertEquals(user, dbUser, "User registered in db");
        assertFalse(dbUser.getGlobalNote().getId() == null, "Initialized global note");
        assertEquals(0, dbUser.getNotes().size(), "Other notes are empty");
    }

    @Test
    @Transactional
    void findAllUsersDataSize() {
        user.getGlobalNote().setBody(STRING_1kB);
        Map<User, Long> allUsersDataSize = userService.findAllUsersDataSize();
        assertEquals(0, allUsersDataSize.get(user).longValue(), "Size of around 0 initially");
        userService.updateUser(user);
        allUsersDataSize = userService.findAllUsersDataSize();
        assertEquals(1, allUsersDataSize.get(user).longValue(), "Size of around 1K after creating note");
    }

    @Test
    @Transactional
    void findNoteDatesByUserId() {
        user.getNotes().add(Note.builder().id(44L).date(LocalDate.now().plusDays(1)).build());
        user.getNotes().add(Note.builder().id(43L).date(LocalDate.now().plusDays(2)).build());
        userRepository.save(user);
        assertEquals(2, userRepository.findNoteDatesByUserId(user.getId()).size());
        assertTrue(userRepository.findNoteDatesByUserId(user.getId()).contains(LocalDate.now().plusDays(1)));
        assertTrue(userRepository.findNoteDatesByUserId(user.getId()).contains(LocalDate.now().plusDays(2)));
        assertFalse(userRepository.findNoteDatesByUserId(user.getId()).contains(LocalDate.now()), "Should not contain global note");
    }

    @Test
    @Transactional
    void getEmails() {
        assertTrue(userService.getUserEmails().contains(user.getEmail()));
    }

    @Test
    @Transactional
    void getUserDataSize() {
        when(sessionService.getAuthenticatedUserId()).thenReturn(user.getId());
        user.getGlobalNote().setBody(STRING_1kB);
        assertEquals(0, userService.getUserDataSize().longValue(), "Size of around 0 initially");
        userService.updateUser(user);
        assertEquals(1, userService.getUserDataSize().longValue(), "Size of around 1K after creating note");
    }
}
