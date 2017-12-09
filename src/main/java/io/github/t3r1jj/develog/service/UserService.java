package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        return getUser("abc");
    }

    @Transactional(readOnly = true)
    public User getUser(String id) {
        return userRepository.getOne(id);
    }

    @Transactional
    public User registerUser(User user) {
        User userToRegister = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getEmail())
                .globalNote(Note.builder()
                        .isGlobal(true)
                        .build()
                ).build();
        return userRepository.save(userToRegister);
    }

}
