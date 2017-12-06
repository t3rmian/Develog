package io.github.t3r1jj.develog;

import io.github.t3r1jj.develog.repository.NoteRepository;
import io.github.t3r1jj.develog.repository.TagRepository;
import io.github.t3r1jj.develog.repository.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Tag("integration")
class DatabaseIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private TagRepository tagRepository;

    @Test
    void dbConnection() {
        System.out.println(userRepository.findAll());
        System.out.println(noteRepository.findAll());
        System.out.println(tagRepository.findAll());
    }
}
