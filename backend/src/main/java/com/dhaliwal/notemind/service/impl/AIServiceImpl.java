package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.service.AIService;
import com.dhaliwal.notemind.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    private final Util util;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${google.api.key}")
    private String genAIApiKey;

    public AIServiceImpl(Util util, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.util = util;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public String getSummaryFromTextGenAI(String title, String content) {
        System.setProperty("GOOGLE_API_KEY", genAIApiKey);
        Client client = Client.builder().apiKey(genAIApiKey).build();
        String prompt = util.getPrompt(title, content);
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3-flash-preview",
                        prompt,
                        null);
        return response.text();
    }
    @Override
    public String getSummary(String title, String content, String imageUrl) {

        String prompt = util.getPrompt(title, content);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openRouterApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = util.buildRequest(prompt, imageUrl);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://openrouter.ai/api/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String response = responseEntity.getBody();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from AI");
            }

            JsonNode root = objectMapper.readTree(response);

            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException("Invalid AI response: no choices");
            }

            String summary = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText("");

            // Clean weird characters
            summary = summary.replaceAll("[^\\x00-\\x7F]", "").trim();

            return summary;

        } catch (Exception e) {
            throw new RuntimeException("Failed to get summary from AI", e);
        }
    }

}

