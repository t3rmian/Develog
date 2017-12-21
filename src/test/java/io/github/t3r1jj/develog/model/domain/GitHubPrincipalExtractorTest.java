package io.github.t3r1jj.develog.model.domain;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.utils.OAuth2Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GitHubPrincipalExtractorTest {

    @Mock
    private OAuth2User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<String, Object> attributes = OAuth2Utils.parseUserDetails(OAuth2Utils.getDefaultUserAttributes());
        when(user.getAttributes()).thenReturn(attributes);
    }

    @Test
    void extract() {
        GitHubPrincipalExtractor extractor = new GitHubPrincipalExtractor();
        User authenticatedUser = extractor.extract(user);
        assertEquals("terlecki-rsi", authenticatedUser.getName(), "Name should match login");
        assertEquals((Long) 25926780L, authenticatedUser.getId(), "Ids should match");
        assertEquals(User.Role.USER, authenticatedUser.getRole(), "Default role is user");
        assertEquals("terlecki-rsi@mail.com", authenticatedUser.getEmail(), "Emails should match");
    }

    @Test
    void extractAdmin() {
        user.getAttributes().put("login", "T3r1jj");
        GitHubPrincipalExtractor extractor = new GitHubPrincipalExtractor();
        User authenticatedUser = extractor.extract(user);
        assertEquals("T3r1jj", authenticatedUser.getName(), "Name should match login");
        assertEquals(User.Role.ADMIN, authenticatedUser.getRole(), "Admin detected by name");
    }

    @Test
    void extractId() {
        assertEquals((Long) 25926780L, GitHubPrincipalExtractor.extractId(user));
    }

}