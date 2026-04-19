package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.service.NotesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {
    public NoteController(NotesService notesService) {
        this.notesService = notesService;
    }

    private final NotesService notesService;
    @PostMapping
    public ResponseEntity<NoteDto> createNote(@RequestBody NoteDto noteDto) {
        return ResponseEntity.ok(notesService.createNote(noteDto));
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getAllNotes(){
        return ResponseEntity.ok(notesService.getAllNotes());
    }
    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable long id){
        return ResponseEntity.ok(notesService.getNoteById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(@RequestBody NoteDto noteDto, @PathVariable long id){
        return ResponseEntity.ok(notesService.updateNote(id, noteDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable long id){
        notesService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam MultipartFile file) {

        String uploadPath = System.getProperty("user.dir") + "/uploads";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        File destination = new File(uploadDir, fileName);

        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        return "http://localhost:8080/api/images/" + fileName;
    }
}
