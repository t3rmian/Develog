package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.domain.Editor;
import io.github.t3r1jj.develog.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private final NoteService noteService;

    @Autowired
    public SearchController(NoteService noteService) {
        this.noteService = noteService;
    }

    @RequestMapping("/search")
    public String showSearch(Model model, @RequestParam(required = false) LocalDate date) {
        if (date != null) {
            model.addAttribute("date", DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        return "search";
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/search/tag", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView findAllByTags(Model model, @RequestParam(value = "values[]", defaultValue = "") List<String> values) {
        List<Note> notes = noteService.findAllByTags(values);
        Editor editor = new Editor();
        editor.setInput(notes);
        model.addAttribute("editor", editor);
        return new ModelAndView("fragments/editor", model.asMap());
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/search/{date}", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView findByDate(Model model, @PathVariable LocalDate date) {
        List<Note> notes = new ArrayList<>();
        noteService.findByDate(date).ifPresent(notes::add);
        Editor editor = new Editor();
        editor.setInput(notes);
        model.addAttribute("editor", editor);
        return new ModelAndView("fragments/editor", model.asMap());
    }

}
