package com.dhaliwal.notemind.dto.quiz.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private String question;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
}