package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.Application;
import io.github.t3r1jj.develog.controller.AdminController;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class AuthorizationAspectTest {

    @Mock
    private UserService userService;
    @Autowired
    private AuthorizationAspect authorizationAspect;
    @Autowired
    private AdminController adminController;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        Field isEnabled = AuthorizationAspect.class.getDeclaredField("userService");
        boolean accessible = isEnabled.isAccessible();
        isEnabled.setAccessible(true);
        isEnabled.set(authorizationAspect, userService);
        isEnabled.setAccessible(accessible);
    }

    @Test
    void authorizeUserOnAdminResource() {
        when(userService.getLoggedUser()).thenReturn(User.builder().id(1L).role(User.Role.USER).build());
        assertThrows(AccessDeniedException.class, () -> adminController.getPage(new ConcurrentModel()));
    }

    @Test
    void authorizeBannedOnAdminResource() {
        when(userService.getLoggedUser()).thenReturn(User.builder().id(1L).role(User.Role.BANNED).build());
        assertThrows(AccessDeniedException.class, () -> adminController.getPage(new ConcurrentModel()));
    }

    @Test
    void authorizeAdminOnAdminResource() {
        when(userService.getLoggedUser()).thenReturn(User.builder().id(1L).role(User.Role.ADMIN).build());
        adminController.getPage(new ConcurrentModel());
    }

    @Test
    void addUserInfoModel() {
        User loggedUser = User.builder().id(1L).role(User.Role.ADMIN).build();
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(userService.isUserAuthenticated()).thenReturn(true);
        ConcurrentModel model = new ConcurrentModel();
        adminController.getPage(model);
        assertEquals(loggedUser, model.get("loggedUser"));
        assertTrue(model.containsAttribute("dataSize"));
    }

    @Test
    void addUserInfoModel_Unauthenticated() {
        User loggedUser = User.builder().id(1L).role(User.Role.ADMIN).build();
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(userService.isUserAuthenticated()).thenReturn(false);
        ConcurrentModel model = new ConcurrentModel();
        adminController.getPage(model);
        assertFalse(model.containsAttribute("loggedUser"));
        assertFalse(model.containsAttribute("dataSize"));
    }

}