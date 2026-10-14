package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.flashCard.request.FlashCardRequestDto;
import com.dhaliwal.notemind.dto.flashCard.response.FlashCardDto;

import java.util.List;


public interface FlashCardService {

    List<FlashCardDto> generateFlashCards(
            Long noteId,
            int count
    );

    List<FlashCardDto> getFlashCardsByNote(
            Long noteId
    );

    FlashCardDto getFlashCard(
            Long flashCardId
    );

    FlashCardDto createFlashCard(
            Long noteId,
            FlashCardRequestDto request
    );

    FlashCardDto updateFlashCard(
            Long flashCardId,
            FlashCardRequestDto request
    );

    Long deleteFlashCard(
            Long flashCardId
    );

    void deleteAllFlashCardsOfNote(
            Long noteId
    );

    List<FlashCardDto> regenerateFlashCards(
            Long noteId,
            int count
    );
}
