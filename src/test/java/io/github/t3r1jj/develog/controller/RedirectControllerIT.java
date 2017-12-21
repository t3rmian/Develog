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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, SpringBootWebSecurityConfiguration.class})
class RedirectControllerIT {

    private static final String AJAX_HEADER = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";
    private static final String BASE_URL = "http://localhost";
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
    void _302Admin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/"));
    }

    @Test
    void _302Note() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/"));
    }

    @Test
    @WithMockOAuth2User
    void _403NotAdmin() throws Exception {
        userService.onAuthenticationSuccess();
        System.out.println(userService.getLoggedUser().getRole());
        mockMvc.perform(get("/admin"))
                .andExpect(status().is(200))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "type", "message"))
                .andExpect(model().attribute("status", "403"));
    }

    @Test
    @WithMockOAuth2User
    void _403BannedAjax() throws Exception {
        userService.onAuthenticationSuccess();
        User loggedUser = userService.getLoggedUser();
        loggedUser.setRole(User.Role.BANNED);
        userService.updateUser(loggedUser);
        mockMvc.perform(post("/note/tag").with(csrf()).header(AJAX_HEADER, AJAX_HEADER_VALUE)
                .param("value", "tag")
                .param("action", NoteController.Action.ADD.toString()))
                .andExpect(status().is(403))
                .andExpect(MockMvcResultMatchers.view().name("error"))
                .andExpect(model().attributeExists("status", "type", "message"))
                .andExpect(model().attribute("status", "403"));
    }

    @Test
    @WithMockOAuth2User
    void defaultErrorHandler() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/note/update"))
                .andExpect(status().is(200))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "type", "message"))
                .andExpect(model().attribute("status", "Unexpected error"));
    }

    @Test
    @WithMockOAuth2User
    void defaultErrorHandlerAjax() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/note/update").header(AJAX_HEADER, AJAX_HEADER_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "type", "message"))
                .andExpect(model().attribute("status", "Unexpected error"));
    }

    @Test
    @WithMockOAuth2User
    void authenticatedNoRedirect() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/note"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note"));
    }

}