package io.github.t3r1jj.develog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RedirectControllerTest {

    private static final String AJAX_HEADER = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";
    private MockMvc mockMvc;
    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");
        RedirectController redirectController = new RedirectController(messageSource);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(redirectController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void index() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }

    @Test
    void about() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("about.html"));
    }

    @Test
    void _404() throws Exception {
        doReturn("message").when(messageSource).getMessage(anyString(), any(Object[].class), any(Locale.class));
        mockMvc.perform(get("/404"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("error.html"))
                .andExpect(model().attributeExists("status", "type", "message"));
    }

    @Test
    void _404Ajax() throws Exception {
        doReturn("message").when(messageSource).getMessage(anyString(), any(Object[].class), any(Locale.class));
        mockMvc.perform(get("/404").header(AJAX_HEADER, AJAX_HEADER_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("error.html"))
                .andExpect(model().attributeExists("status", "type", "message"));
    }

    @Test
    void _403() throws Exception {
        doReturn("message").when(messageSource).getMessage(anyString(), any(Object[].class), any(Locale.class));
        mockMvc.perform(get("/403"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("error.html"))
                .andExpect(model().attributeExists("status", "type", "message"));
    }

    @Test
    void _403Ajax() throws Exception {
        doReturn("message").when(messageSource).getMessage(anyString(), any(Object[].class), any(Locale.class));
        mockMvc.perform(get("/403").header(AJAX_HEADER, AJAX_HEADER_VALUE))
                .andExpect(status().is(403))
                .andExpect(forwardedUrl("error.html"))
                .andExpect(model().attributeExists("status", "type", "message"));
    }

    @Test
    void _302() throws Exception {
        mockMvc.perform(get("/302"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}