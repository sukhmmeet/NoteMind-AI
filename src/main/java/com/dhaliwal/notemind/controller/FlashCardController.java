package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.ApiResponse;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class FlashCardController {

    private final FlashCardService flashCardService;

    @PostMapping("/notes/{noteId}/flashcards/generate")
    public ResponseEntity<ApiResponse<List<FlashCardDto>>> generateFlashCards(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int count) {
        List<FlashCardDto> flashCards = flashCardService.generateFlashCards(noteId, count);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flash cards generated successfully", flashCards));
    }

    @PostMapping("/notes/{noteId}/flashcards/regenerate")
    public ResponseEntity<ApiResponse<List<FlashCardDto>>> regenerateFlashCards(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int count) {
        List<FlashCardDto> flashCards = flashCardService.regenerateFlashCards(noteId, count);
        return ResponseEntity.ok(ApiResponse.success("Flash cards regenerated successfully", flashCards));
    }

    @GetMapping("/notes/{noteId}/flashcards")
    public ResponseEntity<ApiResponse<List<FlashCardDto>>> getFlashCardsByNote(@PathVariable Long noteId) {
        return ResponseEntity.ok(ApiResponse.success(flashCardService.getFlashCardsByNote(noteId)));
    }

    @GetMapping("/flashcards/{flashCardId}")
    public ResponseEntity<ApiResponse<FlashCardDto>> getFlashCard(@PathVariable Long flashCardId) {
        return ResponseEntity.ok(ApiResponse.success(flashCardService.getFlashCard(flashCardId)));
    }

    @PostMapping("/notes/{noteId}/flashcards")
    public ResponseEntity<ApiResponse<FlashCardDto>> createFlashCard(
            @PathVariable Long noteId,
            @Valid @RequestBody FlashCardRequestDto request) {
        FlashCardDto created = flashCardService.createFlashCard(noteId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flash card created successfully", created));
    }

    @PatchMapping("/flashcards/{flashCardId}")
    public ResponseEntity<ApiResponse<FlashCardDto>> updateFlashCard(
            @PathVariable Long flashCardId,
            @RequestBody FlashCardRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Flash card updated successfully",
                flashCardService.updateFlashCard(flashCardId, request)));
    }

    @DeleteMapping("/flashcards/{flashCardId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlashCard(@PathVariable Long flashCardId) {
        flashCardService.deleteFlashCard(flashCardId);
        return ResponseEntity.ok(ApiResponse.success("Flash card deleted successfully"));
    }

    @DeleteMapping("/notes/{noteId}/flashcards")
    public ResponseEntity<ApiResponse<Void>> deleteAllFlashCards(@PathVariable Long noteId) {
        flashCardService.deleteAllFlashCardsOfNote(noteId);
        return ResponseEntity.ok(ApiResponse.success("All flash cards deleted successfully"));
    }
}
