package com.dhaliwal.notemind.dto.flashCard.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashCardRequestDto {
    private String question;
    private String answer;
}
