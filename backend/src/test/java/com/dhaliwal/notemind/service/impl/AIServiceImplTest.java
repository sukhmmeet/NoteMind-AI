package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.dto.flashCard.ai.AIFlashCardResponse;
import com.dhaliwal.notemind.dto.flashCard.ai.Flashcard;
import com.dhaliwal.notemind.entity.type.SummaryType;
import com.dhaliwal.notemind.exception.AiServiceException;
import com.dhaliwal.notemind.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @Mock
    private Util util;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private AIServiceImpl aiService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        aiService = new AIServiceImpl(util, objectMapper, restTemplate);
    }

    @Test
    void getAIResponse_ShouldReturnAiResponse() throws Exception {

        String prompt = "prompt";

        Map<String, Object> request =
                Map.of("model", "model");

        String response =
                """
                {
                  "choices":[
                    {
                      "message":{
                        "content":"{\\"summary\\":\\"Short Summary\\",\\"tags\\":[\\"java\\"]}"
                      }
                    }
                  ]
                }
                """;

        AINoteResponse aiResponse = new AINoteResponse();
        aiResponse.setSummary("Short Summary");

        when(util.getPromptForNoteSummary(any(), any(), any()))
                .thenReturn(prompt);

        when(util.buildRequest(prompt, null))
                .thenReturn(request);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok(response));

        AINoteResponse result =
                aiService.getAIResponse(
                        "Title",
                        "Content",
                        null,
                        SummaryType.SHORT
                );

        assertEquals("Short Summary", result.getSummary());

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getAIResponse_ShouldThrow_WhenResponseIsEmpty() {

        when(util.getPromptForNoteSummary(any(), any(), any()))
                .thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok(""));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIResponse(
                        "Title",
                        "Content",
                        null,
                        SummaryType.SHORT
                )
        );
    }

    @Test
    void getAIResponse_ShouldThrow_WhenChoicesMissing() {

        when(util.getPromptForNoteSummary(any(), any(), any()))
                .thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("{}"));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIResponse(
                        "Title",
                        "Content",
                        null,
                        SummaryType.SHORT
                )
        );
    }

    @Test
    void getAIResponse_ShouldThrow_WhenJsonIsInvalid() {

        String response =
                """
                {
                  "choices":[
                    {
                      "message":{
                        "content":"invalid-json"
                      }
                    }
                  ]
                }
                """;

        when(util.getPromptForNoteSummary(any(), any(), any()))
                .thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok(response));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIResponse(
                        "Title",
                        "Content",
                        null,
                        SummaryType.SHORT
                )
        );
    }

    @Test
    void getAIResponse_ShouldThrow_WhenRestTemplateFails() {

        when(util.getPromptForNoteSummary(any(), any(), any()))
                .thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIResponse(
                        "Title",
                        "Content",
                        null,
                        SummaryType.SHORT
                )
        );
    }
    @Test
    void shouldThrowWhenResponseIsNull() {

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(null));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIFlashCardResponse("t", "c", null, 1)
        );
    }
    @Test
    void shouldThrowWhenResponseIsEmpty() {

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(""));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIFlashCardResponse("t", "c", null, 5)
        );
    }
    @Test
    void shouldThrowWhenChoicesMissing() {

        String response = "{}";

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(response));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIFlashCardResponse("t", "c", null, 5)
        );
    }

    @Test
    void shouldThrowWhenChoicesArrayEmpty() {

        String response = """
    {
      "choices":[]
    }
    """;

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(response));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIFlashCardResponse("t", "c", null, 5)
        );
    }
    @Test
    void shouldThrowWhenFlashCardJsonInvalid() {

        String response = """
    {
      "choices":[
        {
          "message":{
            "content":"invalid json"
          }
        }
      ]
    }
    """;

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(response));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIFlashCardResponse("t", "c", null, 5)
        );
    }
    @Test
    void shouldWrapRestTemplateException() {

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Connection failed"));

        assertThrows(
                AiServiceException.class,
                () -> aiService.getAIFlashCardResponse("t", "c", null, 5)
        );
    }
    @Test
    void shouldCleanUnicodeCharactersBeforeParsing() throws Exception {

        String response = """
        {
          "choices":[
            {
              "message":{
                "content":"🙂{\\"flashcards\\":[]}"
              }
            }
          ]
        }
        """;

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(any(), any()))
                .thenReturn(Map.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(response));

        AIFlashCardResponse result =
                aiService.getAIFlashCardResponse("t", "c", null, 5);

        assertNotNull(result);
        assertTrue(result.getFlashcards().isEmpty());
    }
    @Test
    void shouldGenerateFlashCardsSuccessfully() {

        String title = "Binary Search";
        String content = "Binary Search works on sorted arrays.";
        String imageUrl = null;

        String prompt = "prompt";

        Map<String, Object> request = Map.of();

        String aiJson = """
    {
      "choices":[
        {
          "message":{
            "content":"{\\"flashcards\\":[{\\"question\\":\\"Q1\\",\\"answer\\":\\"A1\\"}]}"
          }
        }
      ]
    }
    """;

        AIFlashCardResponse expected =
                new AIFlashCardResponse(
                        List.of(new Flashcard("Q1", "A1"))
                );

        when(util.getPromptForFlashCard(
                anyString(),
                anyString(),
                anyInt()
        )).thenReturn("prompt");

        when(util.buildRequest(prompt, imageUrl))
                .thenReturn(request);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok(aiJson));

        AIFlashCardResponse result =
                aiService.getAIFlashCardResponse(title, content, imageUrl, 5);

        assertEquals(1, result.getFlashcards().size());

        assertEquals(
                expected.getFlashcards().getFirst().getQuestion(),
                result.getFlashcards().getFirst().getQuestion()
        );

        assertEquals(
                expected.getFlashcards().getFirst().getAnswer(),
                result.getFlashcards().getFirst().getAnswer()
        );
    }

}