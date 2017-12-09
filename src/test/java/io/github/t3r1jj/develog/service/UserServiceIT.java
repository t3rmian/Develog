package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceIT {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void registerUser() {
        User user = User.builder()
                .id("abc").build();
        userService.registerUser(user);

        User dbUser = userRepository.getOne(user.getId());
        assertEquals(user, dbUser, "User registered in db");
        assertFalse(dbUser.getGlobalNote().getId() == null, "Initialized global note");
        assertEquals(0, dbUser.getNotes().size(), "Other notes are empty");
    }

}
