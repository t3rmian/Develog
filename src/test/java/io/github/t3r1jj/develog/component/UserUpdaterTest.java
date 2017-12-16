package io.github.t3r1jj.develog.component;

import io.github.t3r1jj.develog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

class UserUpdaterTest {
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private Authentication authentication;
    private UserUpdater userUpdater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userUpdater = spy(new UserUpdater(userService));
    }

    @Test
    void onAuthenticationSuccess() throws ServletException, IOException {
        userUpdater.onAuthenticationSuccess(httpRequest, httpResponse, authentication);
        verify(userService, times(1)).onAuthenticationSuccess();
    }
}