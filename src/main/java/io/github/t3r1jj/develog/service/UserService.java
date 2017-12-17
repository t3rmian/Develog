package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Transactional
    public User getLoggedUser() {
        return getUser(sessionService.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find the user in db."));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User registerUser(User user) {
        User userToRegister = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .globalNote(Note.builder()
                        .isGlobal(true)
                        .build()
                ).build();
        return userRepository.save(userToRegister);
    }

    /**
     * @return number of characters written by user (notes body, tags) in thousands (1k)
     */
    @Transactional
    public Map<User, Long> findAllUsersDataSize() {
        return userRepository.findAllUsersDataSize().stream()
                .collect(Collectors.toMap(res -> (User) res[0], res -> ((Long) res[1]) / 1000));
    }

    /**
     * @return user data db size [MB]
     */
    @Transactional
    public long getUsersDataSize() {
        try {
            Map<String, Object> dbSize = userRepository.getDbSize();
            return Long.parseLong(dbSize.get("pg_database_size").toString()) / (1024 * 1024);
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Transactional
    public List<LocalDate> getUserNoteDates() {
        return userRepository.findNoteDatesByUserId(getLoggedUser().getId());
    }

    @Transactional
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
        return userRepository.findAllEmails();
    }

    @Transactional
    public Long getLoggedUserDataSize() {
        return userRepository.getUserDataSize(sessionService.getAuthenticatedUserId()) / 1000;
    }
}
