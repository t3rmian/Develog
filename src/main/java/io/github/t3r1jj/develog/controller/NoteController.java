package io.github.t3r1jj.develog.controller;

import io.github.t3r1jj.develog.model.data.Note;
import io.github.t3r1jj.develog.model.domain.Editor;
import io.github.t3r1jj.develog.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @RequestMapping({"/note", "/note/{date}"})
    // TODO: Change to readonly when registering user is implemented
    @Transactional
    public String openNote(Model model, @PathVariable(required = false) LocalDate date) {
        Note note = noteService.getNote(date).get();
        Editor noteEditor = new Editor(note.getBody());
        model.addAttribute("editor", noteEditor);
        model.addAttribute("note", note);
        return "note";
    }

    @Transactional
    @RequestMapping(value = {"/note/update", "/note/{date}/update"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateNote(@ModelAttribute("editor") Editor editor, @PathVariable(required = false) LocalDate date) {
        noteService.updateNoteBody(date, editor.getInput());
        return editor.getOutput();
    }

    @RequestMapping(value = {"/note/tag", "/note/{date}/tag"})
    @ResponseBody
    public Boolean updateTags(@RequestParam String tag, @RequestParam Action action, @PathVariable(required = false) LocalDate date) {
        switch (action) {
            case ADD: {
                return noteService.addNoteTag(date, tag);
            }
            case REMOVE: {
                return noteService.removeNoteTag(date, tag);
            }
            default:
                throw new UnsupportedOperationException();
        }
    }

    enum Action {
        ADD, REMOVE
    }

}
