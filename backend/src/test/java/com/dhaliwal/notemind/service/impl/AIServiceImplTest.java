package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.entity.type.SummaryType;
import com.dhaliwal.notemind.exception.AiServiceException;
import com.dhaliwal.notemind.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @Mock
    private Util util;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AIServiceImpl aiService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        aiService = new AIServiceImpl(util, objectMapper, restTemplate);

        ReflectionTestUtils.setField(
                aiService,
                "openRouterApiKey",
                "test-api-key"
        );
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

        when(util.getPrompt(any(), any(), any()))
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

        when(util.getPrompt(any(), any(), any()))
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

        when(util.getPrompt(any(), any(), any()))
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

        when(util.getPrompt(any(), any(), any()))
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

        when(util.getPrompt(any(), any(), any()))
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
}