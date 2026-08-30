package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.ApiResponse;
import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.type.SummaryType;
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
    public ResponseEntity<ApiResponse<NoteDto>> createNote(
            @RequestPart("note") String noteJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        NoteDto noteDto = objectMapper.readValue(noteJson, NoteDto.class);
        NoteDto savedNoteDto = notesService.createNote(noteDto, image);
        return ResponseEntity.ok(ApiResponse.success("Note created successfully", savedNoteDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteDto>>> getAllNotes() {
        return ResponseEntity.ok(ApiResponse.success(notesService.getAllNotes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteDto>> getNoteById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.success(notesService.getNoteById(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<NoteDto>> updateNote(
            @PathVariable long id,
            @RequestPart("note") String noteJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        NoteDto noteDto = objectMapper.readValue(noteJson, NoteDto.class);
        return ResponseEntity.ok(ApiResponse.success("Note updated successfully", notesService.updateNote(id, noteDto, image)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable long id) {
        notesService.deleteNote(id);
        return ResponseEntity.ok(ApiResponse.success("Note deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NoteDto>>> search(@RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success(notesService.searchNotes(query)));
    }

    @GetMapping("/refresh-summary/{id}")
    public ResponseEntity<ApiResponse<NoteDto>> refreshSummary(@PathVariable long id, @RequestParam SummaryType type) {
        return ResponseEntity.ok(ApiResponse.success("Summary refreshed successfully", notesService.refreshSummary(id, type)));
    }
}
