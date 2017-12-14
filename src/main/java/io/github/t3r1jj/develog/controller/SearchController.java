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
    public String showSearch(Model model) {
        return "search";
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/search/tag", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView findAllByTags(Model model, @RequestParam(value = "values[]", defaultValue = "")  List<String> values) {
        List<Note> notes = noteService.findAllByTags(values);
        model.addAttribute("editor", new Editor(notes));
        return new ModelAndView("fragments/editor", model.asMap());
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/search/date", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView findByDate(Model model, @PathVariable LocalDate value) {
        List<Note> notes = new ArrayList<>();
        noteService.findByDate(value).ifPresent(notes::add);
        model.addAttribute("editor", new Editor(notes));
        return new ModelAndView("fragments/editor", model.asMap());
    }

}
