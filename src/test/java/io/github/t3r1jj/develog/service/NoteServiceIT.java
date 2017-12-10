package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.TagRepository;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NoteServiceIT {
    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(123L).build();
        user = userService.registerUser(user);
    }

    @Test
    @Transactional
    @DisplayName("Db should contain 3 different tags (1 shared) for 2 user notes")
    void addNoteTag() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        user.getOrCreate(yesterday);
        user.getOrCreate(today);
        user = userRepository.save(user);

        noteService.addNoteTag(yesterday, "tag1");
        noteService.addNoteTag(yesterday, "tag2");
        noteService.addNoteTag(today, "tag2");
        noteService.addNoteTag(today, "tag3");

        assertEquals(3, tagRepository.findAll().size(), "DB should have 3 different tags");
    }
}
