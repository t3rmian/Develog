package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.Tag;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.Editor;
import io.github.t3r1jj.develog.service.NoteService;
import io.github.t3r1jj.develog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;

    @Autowired
    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
        userService.registerUser(User.builder().id("abc").build());
    }

    @RequestMapping({"/note", "/note/{date}"})
    public String openNote(Model model, @PathVariable(required = false) LocalDate date) {
        Note note = userService.getLoggedUser().getNote(date).get();
        noteService.updateNote(note);
        Editor noteEditor = new Editor(note.getBody());
        model.addAttribute("editor", noteEditor);
        model.addAttribute("note", note);
        return "note";
    }

    @Transactional
    @RequestMapping(value = {"/note/update", "/note/{date}/update"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateNote(@ModelAttribute("editor") Editor editor, @PathVariable(required = false) LocalDate date) {
        Note note = userService.getLoggedUser().getNote(date).get();
        note.setBody(editor.getInput());
        noteService.updateNote(note);
        return editor.getOutput();
    }

    @RequestMapping(value = {"/note/tag", "/note/{date}/tag"})
    @ResponseBody
    public Boolean updateTags(@RequestParam String tag, @RequestParam Action action, @PathVariable(required = false) LocalDate date) {
        Note note = userService.getLoggedUser().getNote(date).get();
        boolean change;
        switch (action) {
            case ADD: {
                change = note.addTag(new Tag(tag));
                break;
            }
            case REMOVE: {
                change = note.removeTag(new Tag(tag));
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        if (change) {
            noteService.updateNote(note);
        }
        return change;
    }

    enum Action {
        ADD, REMOVE
    }

}
