package com.dhaliwal.notemind.dto.flashCard.ai;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFlashCardResponse {
    private List<Flashcard> flashcards;
}
