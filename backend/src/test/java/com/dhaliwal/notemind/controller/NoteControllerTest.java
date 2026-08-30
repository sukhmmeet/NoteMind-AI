package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.type.SummaryType;
import com.dhaliwal.notemind.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class NoteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotesService notesService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new NoteController(notesService, objectMapper)).build();
    }

    @Test
    void testCreateNote() throws Exception {
        NoteDto requestDto = new NoteDto();
        requestDto.setTitle("New Note");
        requestDto.setContent("Content");

        MockMultipartFile notePart = new MockMultipartFile(
                "note", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "image", "image.png", MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        NoteDto responseDto = new NoteDto();
        responseDto.setId(1L);
        responseDto.setTitle("New Note");

        when(notesService.createNote(any(NoteDto.class), any())).thenReturn(responseDto);

        mockMvc.perform(multipart("/notes")
                .file(notePart)
                .file(imagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Note created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("New Note"));
    }

    @Test
    void testGetAllNotes() throws Exception {
        NoteDto note1 = new NoteDto(); note1.setId(1L); note1.setTitle("Note 1");
        NoteDto note2 = new NoteDto(); note2.setId(2L); note2.setTitle("Note 2");

        when(notesService.getAllNotes()).thenReturn(List.of(note1, note2));

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Note 1"));
    }

    @Test
    void testGetNoteById() throws Exception {
        NoteDto note = new NoteDto(); note.setId(1L); note.setTitle("Note 1");

        when(notesService.getNoteById(1L)).thenReturn(note);

        mockMvc.perform(get("/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Note 1"));
    }

    @Test
    void testUpdateNote() throws Exception {
        NoteDto requestDto = new NoteDto();
        requestDto.setTitle("Updated Note");

        MockMultipartFile notePart = new MockMultipartFile(
                "note", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        NoteDto responseDto = new NoteDto();
        responseDto.setId(1L);
        responseDto.setTitle("Updated Note");

        when(notesService.updateNote(eq(1L), any(NoteDto.class), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(HttpMethod.PUT, "/notes/1")
                .file(notePart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Note updated successfully"))
                .andExpect(jsonPath("$.data.title").value("Updated Note"));
    }

    @Test
    void testDeleteNote() throws Exception {
        doNothing().when(notesService).deleteNote(1L);

        mockMvc.perform(delete("/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Note deleted successfully"));
    }

    @Test
    void testSearchNotes() throws Exception {
        NoteDto note = new NoteDto(); note.setId(1L); note.setTitle("Search Result");

        when(notesService.searchNotes("Search")).thenReturn(List.of(note));

        mockMvc.perform(get("/notes/search").param("query", "Search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Search Result"));
    }

    @Test
    void testRefreshSummary() throws Exception {
        NoteDto note = new NoteDto(); note.setId(1L); note.setTitle("Summary Note");

        when(notesService.refreshSummary(eq(1L), eq(SummaryType.SHORT))).thenReturn(note);

        mockMvc.perform(get("/notes/refresh-summary/1").param("type", "SHORT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Summary refreshed successfully"))
                .andExpect(jsonPath("$.data.title").value("Summary Note"));
    }
}
