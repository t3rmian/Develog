package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final User loggedUserStub = User.builder().id("abc").build();
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        return loggedUserStub;
    }

}
