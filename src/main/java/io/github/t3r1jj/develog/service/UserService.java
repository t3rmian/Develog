package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.GitHubPrincipalExtractor;
import io.github.t3r1jj.develog.repository.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        return getUser(getAuthenticatedUserId()).orElseThrow(() -> new UsernameNotFoundException("Couldn't find the user in db."));
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

    @Transactional
    public List<LocalDate> getUserNoteDates() {
        return userRepository.findNoteDatesByUserId(getLoggedUser().getId());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        return new GitHubPrincipalExtractor().extract(token.getPrincipal());
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        return GitHubPrincipalExtractor.extractId(token.getPrincipal());
    }

    public void onAuthenticationSuccess() {
        User authenticatedUser = getAuthenticatedUser();
        User dbUser = getUser(authenticatedUser.getId()).orElseGet(() -> registerUser(authenticatedUser));
        if (!authenticatedUser.infoEquals(dbUser)) {
            updateUser(User.builder()
                    .id(authenticatedUser.getId())
                    .globalNote(dbUser.getGlobalNote())
                    .email(authenticatedUser.getEmail())
                    .name(authenticatedUser.getName())
                    .notes(authenticatedUser.getNotes())
                    .role(dbUser.getRole())
                    .build()
            );
        }
    }
}
