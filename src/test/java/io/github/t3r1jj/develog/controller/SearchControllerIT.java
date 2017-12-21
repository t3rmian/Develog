package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.Application;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.format.DateTimeFormatter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, SpringBootWebSecurityConfiguration.class})
class SearchControllerIT {

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
    void showSearch() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"));
    }

    @Test
    @WithMockOAuth2User
    void showSearchByDate() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/search").param("date", "2000-01-02"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("date", "2000-01-02"));
    }

    @Test
    @WithMockOAuth2User
    void findAllByTags() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(post("/search/tag").with(csrf())
                .param("values[]", "tag"))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/editor"))
                .andExpect(model().attributeExists("editor"));
    }

    @Test
    @WithMockOAuth2User
    void findByDate() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(post("/search/1999-10-10").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/editor"))
                .andExpect(model().attributeExists("date", "editor"));
    }

}