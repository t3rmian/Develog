package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User getLoggedUser() {
        return getUser(123L).orElseGet(() -> registerUser(User.builder().id(123L).build()));
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

    public List<LocalDate> getUserNoteDates() {
        return userRepository.findNoteDatesByUserId(getLoggedUser().getId());
    }

}
