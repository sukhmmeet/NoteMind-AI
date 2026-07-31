package com.dhaliwal.notemind.dto.flashCard.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FlashCardDto {
    private Long id;
    private String question;
    private String answer;
    private Long noteId;
    private LocalDateTime createdAt;
}
