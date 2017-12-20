package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.BusinessRoles;
import io.github.t3r1jj.develog.model.domain.Editor;
import io.github.t3r1jj.develog.model.domain.exception.ResourceNotFoundException;
import io.github.t3r1jj.develog.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.github.t3r1jj.develog.controller.NoteController.Action.ADD;

@Controller
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @BusinessRoles(values = {User.Role.USER, User.Role.ADMIN})
    @RequestMapping({"/today/{date}"})
    public ModelAndView todayNote(@PathVariable LocalDate date) {
        noteService.getNoteOrCreate(date);
        return new ModelAndView("redirect:/note/" + DateTimeFormatter.ISO_LOCAL_DATE.format(date));
    }

    @RequestMapping({"/note", "/note/{date}"})
    public String openNote(Model model, @PathVariable(required = false) LocalDate date) {
        Note note = noteService.getNote(date).orElseThrow(ResourceNotFoundException::new);
        Editor noteEditor = new Editor(note.getBody());
        model.addAttribute("editor", noteEditor);
        model.addAttribute("note", note);
        model.addAttribute("tags", noteService.getAllTags());
        return "note";
    }

    @BusinessRoles(values = {User.Role.USER, User.Role.ADMIN})
    @RequestMapping(value = {"/note/update", "/note/{date}/update"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateNote(@ModelAttribute("editor") Editor editor, @PathVariable(required = false) LocalDate date) {
        noteService.updateNoteBody(date, editor.getInput());
        return editor.getOutput();
    }

    @BusinessRoles(values = {User.Role.USER, User.Role.ADMIN})
    @RequestMapping(value = {"/note/tag", "/note/{date}/tag"}, method = RequestMethod.POST)
    @ResponseBody
    public Boolean updateTags(@RequestParam String value, @RequestParam Action action, @PathVariable(required = false) LocalDate date) {
        if (action == ADD) {
            return noteService.addNoteTag(date, value);
        } else {
            return noteService.removeNoteTag(date, value);
        }
    }

    @RequestMapping("notes")
    @ResponseBody
    public List<LocalDate> getAllNoteDates() {
        return noteService.getNoteDates();
    }

    enum Action {
        ADD, REMOVE
    }

}

