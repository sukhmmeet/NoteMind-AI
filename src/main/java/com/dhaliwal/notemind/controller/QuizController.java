package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.ApiResponse;
import com.dhaliwal.notemind.dto.quiz.response.QuizQuestionDto;
import com.dhaliwal.notemind.service.QuizService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/notes/{noteId}/quiz/generate")
    public ResponseEntity<ApiResponse<List<QuizQuestionDto>>> generateQuiz(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int count) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quiz generated successfully", quizService.generateQuiz(noteId, count)));
    }
}