package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.domain.Note;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@Controller
public class NoteController {
    @RequestMapping("/note")
    public String hello(Model model) {
        model.addAttribute("note", Note.builder()
                .creationTime(Instant.now())
                .body("NOTE BODY")
                .build());
        return "note";
    }
}
