package io.github.t3r1jj.develog;

import io.github.t3r1jj.develog.repository.data.NoteRepository;
import io.github.t3r1jj.develog.repository.data.TagRepository;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import io.github.t3r1jj.develog.repository.monitoring.EventRepository;
import io.github.t3r1jj.develog.repository.monitoring.CallRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class DatabaseIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ErrorRepository errorRepository;
    @Autowired
    private CallRepository callRepository;
    @Autowired
    private EventRepository eventRepository;

    @Test
    @Transactional(readOnly = true)
    void dbConnection() {
        System.out.println(userRepository.findAll());
        System.out.println(noteRepository.findAll());
        System.out.println(tagRepository.findAll());
    }

    @Test
    void monitoringDbConnection() {
        System.out.println(errorRepository.findAll());
        System.out.println(callRepository.findAll());
        System.out.println(eventRepository.findAll());
    }
}
