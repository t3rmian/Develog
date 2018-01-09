package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.Application;
import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.repository.data.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, SpringBootWebSecurityConfiguration.class})
class NoteControllerIT {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        userRepository.deleteById(25926780L);
    }

    @Test
    @WithMockOAuth2User
    @Transactional
    void todayNote() throws Exception {
        userService.onAuthenticationSuccess();
        Long userId = userService.getLoggedUser().getId();
        mockMvc.perform(get("/today/1999-10-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/note/1999-10-10"));
        List<Note> notes = userService.getUser(userId).get().getNotes();
        LocalDate date = LocalDate.of(1999, 10, 10);
        assertTrue(notes.stream().anyMatch(n -> date.equals(n.getDate())));
    }

    @Test
    @WithMockOAuth2User
    void openNote() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/note"))
                .andExpect(status().isOk())
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("editor", "tags", "note"));
    }

    @Test
    @WithMockOAuth2User
    void openNoteByDate_404() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/note/1999-10-10"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "type", "message"))
                .andExpect(model().attribute("status", "404"));
    }

    @Test
    @WithMockOAuth2User
    @Transactional
    void openNoteByDate() throws Exception {
        userService.onAuthenticationSuccess();
        User loggedUser = userService.getLoggedUser();
        loggedUser.getNotes().add(Note.builder().build());
        userService.updateUser(loggedUser);
        mockMvc.perform(get("/note/" + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())))
                .andExpect(status().isOk())
                .andExpect(view().name("note"))
                .andExpect(model().attributeExists("editor", "tags", "note"));
    }

    @Test
    @WithMockOAuth2User
    void updateNote() throws Exception {
        userService.onAuthenticationSuccess();
        Long userId = userService.getLoggedUser().getId();
        mockMvc.perform(post("/note/update")
                .with(csrf())
                .param("input", "a")
                .param("output", "b"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("a")));
        String body = userService.getUser(userId).get().getGlobalNote().getBody();
        assertEquals("a", body);
    }

    @Test
    @WithMockOAuth2User
    @Transactional
    void updateNoteByDate() throws Exception {
        userService.onAuthenticationSuccess();
        User loggedUser = userService.getLoggedUser();
        loggedUser.getNotes().add(Note.builder().build());
        userService.updateUser(loggedUser);
        userService.onAuthenticationSuccess();
        mockMvc.perform(post("/note/" + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()) + "/update")
                .with(csrf())
                .param("input", "a")
                .param("output", "b"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("a")));
        List<Note> notes = userService.getUser(loggedUser.getId()).get().getNotes();
        assertEquals("a", notes.get(0).getBody());
    }

    @Test
    @WithMockOAuth2User
    @Transactional
    void addTags() throws Exception {
        userService.onAuthenticationSuccess();
        Long userId = userService.getLoggedUser().getId();
        mockMvc.perform(post("/note/tag")
                .with(csrf())
                .param("value", "tag")
                .param("action", NoteController.Action.ADD.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        Set<Tag> tags = userService.getUser(userId).get().getGlobalNote().getTags();
        assertEquals("tag", tags.iterator().next().getId().getValue());
    }

    @Test
    @WithMockOAuth2User
    @Transactional
    void removeTags() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(post("/note/tag")
                .with(csrf())
                .param("value", "tag")
                .param("action", NoteController.Action.REMOVE.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockOAuth2User
    @Transactional
    void updateTagsByDate() throws Exception {
        userService.onAuthenticationSuccess();
        User loggedUser = userService.getLoggedUser();
        loggedUser.getNotes().add(Note.builder().build());
        userService.updateUser(loggedUser);

        mockMvc.perform(post("/note/" + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()) + "/tag")
                .with(csrf())
                .param("value", "tag")
                .param("action", NoteController.Action.ADD.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        List<Note> notes = userService.getUser(loggedUser.getId()).get().getNotes();
        Set<Tag> tags = notes.get(0).getTags();
        assertEquals("tag", tags.iterator().next().getId().getValue());
    }

    @Test
    @WithMockOAuth2User
    void getAllNoteDates() throws Exception {
        userService.onAuthenticationSuccess();
        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("[")))
                .andExpect(content().string(endsWith("]")));
    }
}