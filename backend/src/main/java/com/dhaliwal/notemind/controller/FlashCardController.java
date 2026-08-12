package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.flashCard.request.FlashCardRequestDto;
import com.dhaliwal.notemind.dto.flashCard.response.FlashCardDto;
import com.dhaliwal.notemind.service.FlashCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class FlashCardController {

    private final FlashCardService flashCardService;

    @PostMapping("/notes/{noteId}/flashcards/generate")
    public ResponseEntity<List<FlashCardDto>> generateFlashCards(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int count) {
        List<FlashCardDto> flashCards = flashCardService.generateFlashCards(noteId, count);
        return ResponseEntity.status(HttpStatus.CREATED).body(flashCards);
    }

    @PostMapping("/notes/{noteId}/flashcards/regenerate")
    public ResponseEntity<List<FlashCardDto>> regenerateFlashCards(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int count) {
        List<FlashCardDto> flashCards = flashCardService.regenerateFlashCards(noteId, count);
        return ResponseEntity.ok(flashCards);
    }

    @GetMapping("/notes/{noteId}/flashcards")
    public ResponseEntity<List<FlashCardDto>> getFlashCardsByNote(@PathVariable Long noteId) {
        return ResponseEntity.ok(flashCardService.getFlashCardsByNote(noteId));
    }

    @GetMapping("/flashcards/{flashCardId}")
    public ResponseEntity<FlashCardDto> getFlashCard(@PathVariable Long flashCardId) {
        return ResponseEntity.ok(flashCardService.getFlashCard(flashCardId));
    }

    @PostMapping("/notes/{noteId}/flashcards")
    public ResponseEntity<FlashCardDto> createFlashCard(
            @PathVariable Long noteId,
            @Valid @RequestBody FlashCardRequestDto request) {
        FlashCardDto created = flashCardService.createFlashCard(noteId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/flashcards/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/flashcards/{flashCardId}")
    public ResponseEntity<FlashCardDto> updateFlashCard(
            @PathVariable Long flashCardId,
            @RequestBody FlashCardRequestDto request) {
        return ResponseEntity.ok(flashCardService.updateFlashCard(flashCardId, request));
    }

    @DeleteMapping("/flashcards/{flashCardId}")
    public ResponseEntity<Void> deleteFlashCard(@PathVariable Long flashCardId) {
        flashCardService.deleteFlashCard(flashCardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notes/{noteId}/flashcards")
    public ResponseEntity<Void> deleteAllFlashCards(@PathVariable Long noteId) {
        flashCardService.deleteAllFlashCardsOfNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
