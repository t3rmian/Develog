package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.Application;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.service.UserService;
import io.github.t3r1jj.develog.utils.WithMockOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, SpringBootWebSecurityConfiguration.class})
class AdminControllerIT {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockOAuth2User
    void getPage() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    @WithMockOAuth2User
    void getUsersFragment() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockOAuth2User
    void getEventsFragment() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/events"))
                .andExpect(model().attributeExists("events"));
    }

    @Test
    @WithMockOAuth2User
    void getErrorsFragment() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/errors"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("[")))
                .andExpect(content().string(endsWith("]")));
    }

    @Test
    @WithMockOAuth2User
    void getLogsFragment() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("[")))
                .andExpect(content().string(endsWith("]")));
    }

    @Test
    @WithMockOAuth2User
    void getLogsFragmentContent() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("[")))
                .andExpect(content().string(endsWith("]")));
    }

    @Test
    @WithMockOAuth2User
    void getHealthFragment() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..mongo.status").value("UP"))
                .andExpect(jsonPath("$..db.status").value("UP"))
                .andExpect(jsonPath("$..['Mongo DB size']").isNotEmpty())
                .andExpect(jsonPath("$..['Postgres DB size']").isNotEmpty());
    }

    @Test
    @WithMockOAuth2User
    void clearLogs() throws Exception {
        initAdmin();
        mockMvc.perform(get("/admin/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));
    }

    @Test
    @WithMockOAuth2User
    void changeRole() throws Exception {
        initAdmin();
        Long userId = userService.getLoggedUser().getId();
        mockMvc.perform(get("/admin/user")
                .param("id", userId.toString())
                .param("role", User.Role.BANNED.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        assertEquals(User.Role.BANNED, userService.getUser(userId).get().getRole());
    }

    @Test
    @WithMockOAuth2User
    void getEmail() throws Exception {
        initAdmin();
        User loggedUser = userService.getLoggedUser();
        Long userId = loggedUser.getId();
        mockMvc.perform(get("/admin/email")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(loggedUser.getEmail()));
    }

    @Test
    @WithMockOAuth2User
    void getEmails() throws Exception {
        initAdmin();
        User loggedUser = userService.getLoggedUser();
        mockMvc.perform(get("/admin/email/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(loggedUser.getEmail()));
    }

    private void initAdmin() {
        userService.onAuthenticationSuccess();
        User loggedUser = userService.getLoggedUser();
        loggedUser.setRole(User.Role.ADMIN);
        userService.updateUser(loggedUser);
    }
}