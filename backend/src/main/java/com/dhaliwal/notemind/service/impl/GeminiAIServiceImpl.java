package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.service.GeminiAIService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiAIServiceImpl implements GeminiAIService {
    @Value("${google.api.key}")
    private String apiKey;

    @Override
    public String getSummaryFromText(String title, String content) {
        System.setProperty("GOOGLE_API_KEY", apiKey);
        Client client = Client.builder().apiKey(apiKey).build();
        String prompt = "You are an assistant that summarizes notes.\n" +
                "Given the following note:\n\n" +
                "Title: " + title + "\n" +
                "Content: " + content + "\n\n" +
                "Instructions:\n" +
                "- Generate a concise summary in 2-3 sentences.\n" +
                "- Capture the key idea and important details.\n" +
                "- Do not repeat the title.\n" +
                "- Keep it clear and simple.\n\n" +
                "Summary:";
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3-flash-preview",
                        prompt,
                        null);
        return response.text();
    }
}
