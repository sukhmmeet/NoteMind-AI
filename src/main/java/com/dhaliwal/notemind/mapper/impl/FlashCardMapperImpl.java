package com.dhaliwal.notemind.mapper.impl;

import com.dhaliwal.notemind.dto.flashCard.ai.AIFlashCardResponse;
import com.dhaliwal.notemind.dto.flashCard.ai.Flashcard;
import com.dhaliwal.notemind.dto.flashCard.response.FlashCardDto;
import com.dhaliwal.notemind.entity.FlashCard;
import com.dhaliwal.notemind.mapper.FlashCardMapper;
import org.springframework.stereotype.Component;

@Component
public class FlashCardMapperImpl implements FlashCardMapper {
    @Override
    public FlashCard toEntity(Flashcard flashcard) {
        return FlashCard.builder()
                .question(flashcard.getQuestion())
                .answer(flashcard.getAnswer())
                .build();
    }

    @Override
    public FlashCardDto toDto(FlashCard flashCard) {
        return FlashCardDto.builder()
                .question(flashCard.getQuestion())
                .answer(flashCard.getAnswer())
                .id(flashCard.getId())
                .noteId(flashCard.getNote().getId())
                .createdAt(flashCard.getCreatedAt())
                .build();
    }
}
