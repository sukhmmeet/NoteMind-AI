package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.quiz.response.QuizQuestionDto;
import com.dhaliwal.notemind.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new QuizController(quizService)).build();
    }

    @Test
    void generateQuizReturnsQuestions() throws Exception {
        QuizQuestionDto question = QuizQuestionDto.builder()
                .question("What is Java?")
                .options(List.of("Language", "Database", "OS", "Protocol"))
                .correctAnswer("Language")
                .explanation("Java is a programming language.")
                .build();

        when(quizService.generateQuiz(eq(1L), eq(5))).thenReturn(List.of(question));

        mockMvc.perform(post("/notes/1/quiz/generate").param("count", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Quiz generated successfully"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].question").value("What is Java?"))
                .andExpect(jsonPath("$.data[0].options.length()").value(4))
                .andExpect(jsonPath("$.data[0].correctAnswer").value("Language"));
    }
}