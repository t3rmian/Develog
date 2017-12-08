package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.Editor;
import io.github.t3r1jj.develog.service.NoteService;
import io.github.t3r1jj.develog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;

    @Autowired
    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @RequestMapping("/note")
    public String openNote(Model model) {
        Note globalNote = getNote();
        Editor noteEditor = new Editor(globalNote.getBody());
        model.addAttribute("editor", noteEditor);
        model.addAttribute("note", globalNote);
        return "note";
    }

    @RequestMapping(value = "/note/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateNote(@ModelAttribute("note") Note note, @ModelAttribute("editor") Editor editor) {
        note.setBody(editor.getInput());
        noteService.updateNote(note);
        return editor.getOutput();
    }

    @ModelAttribute
    public Note getNote() {
        User loggedUser = userService.getLoggedUser();
        return loggedUser.getGlobalNote();
    }

}
