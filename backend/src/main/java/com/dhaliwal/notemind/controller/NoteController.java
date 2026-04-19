package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.service.NotesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteController {
    public NoteController(NotesService notesService) {
        this.notesService = notesService;
    }

    private final NotesService notesService;
    @PostMapping
    public NoteDto createNote(@RequestBody NoteDto noteDto) {
        return notesService.createNote(noteDto);
    }
}
