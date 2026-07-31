package com.dhaliwal.notemind.mapper;

import com.dhaliwal.notemind.dto.flashCard.ai.Flashcard;
import com.dhaliwal.notemind.dto.flashCard.response.FlashCardDto;
import com.dhaliwal.notemind.entity.FlashCard;

public interface FlashCardMapper {
    FlashCard toEntity(Flashcard flashcard);
    FlashCardDto toDto(FlashCard flashCard);
}
