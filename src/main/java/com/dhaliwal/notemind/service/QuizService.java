package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.quiz.response.QuizQuestionDto;

import java.util.List;

public interface QuizService {
    List<QuizQuestionDto> generateQuiz(Long noteId, int count);
}