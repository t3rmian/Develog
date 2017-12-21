package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.domain.exception.UnauthenticatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SessionServiceTest {

    @Mock
    private OAuth2User user;
    @Mock
    private Map<String, Object> attributes;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(user.getAttributes()).thenReturn(attributes);
        when(attributes.get(any(String.class))).thenReturn("1");
        sessionService = new SessionService();
    }

    @Test
    void getAuthenticatedUser() {
        setUpAuthentication();
        assertTrue(sessionService.getAuthenticatedUser() != null);
    }

    private void setUpAuthentication() {
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(user, null, "github");
        token.setAuthenticated(true);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getAuthenticatedUser_UnauthenticatedException() {
        assertThrowsUnauthenticatedException_Anonymous(sessionService::getAuthenticatedUser);
    }

    @Test
    void getAuthenticatedUserId() {
        setUpAuthentication();
        assertEquals((Long) 1L, sessionService.getAuthenticatedUserId());
    }

    @Test
    void getAuthenticatedUserId_UnauthenticatedException() {
        assertThrowsUnauthenticatedException_Anonymous(sessionService::getAuthenticatedUserId);
    }

    private void assertThrowsUnauthenticatedException_Anonymous(Executable executable) {
        setUpAuthentication_Anonymous();
        assertThrows(UnauthenticatedException.class, executable);
        try {
            executable.execute();
            fail("Should have thrown an exception");
        } catch (Throwable throwable) {
            assertTrue(throwable instanceof UnauthenticatedException, "Correct exception");
            UnauthenticatedException exception = (UnauthenticatedException) throwable;
            assertTrue(exception.getMessage().toLowerCase().contains("auth"), "Contains auth msg");
        }
    }

    private void setUpAuthentication_Anonymous() {
        Authentication token = new AnonymousAuthenticationToken("anonymous", "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        token.setAuthenticated(true);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void isSessionAuthenticated() {
        setUpAuthentication();
        assertTrue(sessionService.isSessionAuthenticated());
    }

    @Test
    void isSessionAuthenticated_Anonymous() {
        setUpAuthentication_Anonymous();
        assertFalse(sessionService.isSessionAuthenticated());
    }

    @Test
    void isSessionAuthenticated_Null() {
        setUpAuthentication_Anonymous();
        SecurityContextHolder.getContext().setAuthentication(null);
        assertFalse(sessionService.isSessionAuthenticated());
    }
}