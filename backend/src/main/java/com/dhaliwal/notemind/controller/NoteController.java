package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NotesService notesService;
    private final ObjectMapper objectMapper;

    public NoteController(NotesService notesService, ObjectMapper objectMapper) {
        this.notesService = notesService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNote(
            @RequestPart("note") String noteJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        NoteDto noteDto = objectMapper.readValue(noteJson, NoteDto.class);

        NoteDto savedNoteDto = notesService.createNote(noteDto, image);
        return ResponseEntity.ok(savedNoteDto);
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getAllNotes(){
        return ResponseEntity.ok(notesService.getAllNotes());
    }
    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable long id){
        return ResponseEntity.ok(notesService.getNoteById(id));
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteDto> updateNote(
            @PathVariable long id,
            @RequestPart("note") String noteJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {

        NoteDto noteDto = objectMapper.readValue(noteJson, NoteDto.class);
        return ResponseEntity.ok(notesService.updateNote(id, noteDto, image));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable long id){
        notesService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/test")
    public ResponseEntity<String> testAI(){

        return ResponseEntity.ok("");
    }
}
