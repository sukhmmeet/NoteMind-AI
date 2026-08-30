package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.flashCard.request.FlashCardRequestDto;
import com.dhaliwal.notemind.dto.flashCard.response.FlashCardDto;
import com.dhaliwal.notemind.service.FlashCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
public class FlashCardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlashCardService flashCardService;

    @InjectMocks
    private FlashCardController flashCardController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flashCardController).build();
    }

    @Test
    void testGenerateFlashCards() throws Exception {
        FlashCardDto fc = FlashCardDto.builder().id(1L).question("Q1").answer("A1").build();

        when(flashCardService.generateFlashCards(eq(1L), eq(5))).thenReturn(List.of(fc));

        mockMvc.perform(post("/notes/1/flashcards/generate")
                .param("count", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flash cards generated successfully"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].question").value("Q1"));
    }

    @Test
    void testRegenerateFlashCards() throws Exception {
        FlashCardDto fc = FlashCardDto.builder().id(2L).question("Q2").answer("A2").build();

        when(flashCardService.regenerateFlashCards(eq(1L), eq(5))).thenReturn(List.of(fc));

        mockMvc.perform(post("/notes/1/flashcards/regenerate")
                .param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flash cards regenerated successfully"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].question").value("Q2"));
    }

    @Test
    void testGetFlashCardsByNote() throws Exception {
        FlashCardDto fc = FlashCardDto.builder().id(1L).question("Q1").answer("A1").build();

        when(flashCardService.getFlashCardsByNote(1L)).thenReturn(List.of(fc));

        mockMvc.perform(get("/notes/1/flashcards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void testGetFlashCard() throws Exception {
        FlashCardDto fc = FlashCardDto.builder().id(1L).question("Q1").answer("A1").build();

        when(flashCardService.getFlashCard(1L)).thenReturn(fc);

        mockMvc.perform(get("/flashcards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.question").value("Q1"));
    }

    @Test
    void testCreateFlashCard() throws Exception {
        FlashCardRequestDto request = new FlashCardRequestDto();
        request.setQuestion("New Q");
        request.setAnswer("New A");

        FlashCardDto response = FlashCardDto.builder().id(1L).question("New Q").answer("New A").build();

        when(flashCardService.createFlashCard(eq(1L), any(FlashCardRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/notes/1/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flash card created successfully"))
                .andExpect(jsonPath("$.data.question").value("New Q"));
    }

    @Test
    void testUpdateFlashCard() throws Exception {
        FlashCardRequestDto request = new FlashCardRequestDto();
        request.setQuestion("Updated Q");

        FlashCardDto response = FlashCardDto.builder().id(1L).question("Updated Q").answer("A1").build();

        when(flashCardService.updateFlashCard(eq(1L), any(FlashCardRequestDto.class))).thenReturn(response);

        mockMvc.perform(patch("/flashcards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flash card updated successfully"))
                .andExpect(jsonPath("$.data.question").value("Updated Q"));
    }

    @Test
    void testDeleteFlashCard() throws Exception {
        when(flashCardService.deleteFlashCard(1L)).thenReturn(1L);

        mockMvc.perform(delete("/flashcards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flash card deleted successfully"));
    }

    @Test
    void testDeleteAllFlashCards() throws Exception {
        doNothing().when(flashCardService).deleteAllFlashCardsOfNote(1L);

        mockMvc.perform(delete("/notes/1/flashcards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All flash cards deleted successfully"));
    }
}
