package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(SessionService sessionService, UserRepository userRepository) {

        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        return getUser(sessionService.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find the user in db."));
    }

    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User registerUser(User user) {
        User userToRegister = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .globalNote(Note.builder()
                        .build()
                ).build();
        return userRepository.save(userToRegister);
    }

    public List<LocalDate> getUserNoteDates() {
        return getLoggedUser().getNoteDates().stream().map(HashMap.Entry::getValue).collect(Collectors.toList());
    }

    public void onAuthenticationSuccess() {
        User authenticatedUser = sessionService.getAuthenticatedUser();
        User dbUser = getUser(authenticatedUser.getId()).orElseGet(() -> registerUser(authenticatedUser));
        if (!authenticatedUser.infoEquals(dbUser)) {
            dbUser.setName(authenticatedUser.getName());
            dbUser.setEmail(authenticatedUser.getEmail());
            updateUser(dbUser);
        }
    }

    public boolean isUserAuthenticated() {
        return sessionService.isSessionAuthenticated();
    }


    public boolean changeRole(Long id, User.Role role) {
        Optional<User> queriedUser = getUser(id);
        if (queriedUser.isPresent()) {
            User user = queriedUser.get();
            user.setRole(role);
            updateUser(user);
            return true;
        } else {
            return false;
        }
    }

    public List<String> getUserEmails() {
        return userRepository.findAllProjectedDistinctEmailBy().stream()
                .map(UserRepository.EmailProjection::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }
}
