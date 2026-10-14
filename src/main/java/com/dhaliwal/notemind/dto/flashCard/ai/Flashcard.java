package com.dhaliwal.notemind.dto.flashCard.ai;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flashcard {

    private String question;
    private String answer;
}
