package com.dhaliwal.notemind.util;

import com.dhaliwal.notemind.entity.type.SummaryType;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class Util {
    public String getPrompt(String title, String content) {
        return """
            You are an assistant that summarizes notes.

            Given the following note:

            Title: %s
            Content: %s

            Instructions:
            - Generate a concise summary in 2-3 sentences.
            - Capture the key idea and important details.
            - Generate 3-5 relevant tags.
            - Tags should be short keywords.
            - Do not repeat the title.
            - Keep everything clear and simple.

            Return ONLY valid JSON in this format:
            {
              "summary": "your summary here",
              "tags": ["tag1", "tag2", "tag3"]
            }
            """.formatted(title, content);
    }
    public String getPrompt(String title, String content, SummaryType summaryType) {

        String summaryInstruction = switch (summaryType) {
            case SHORT -> """
                - Generate a concise summary in 2-3 sentences.
                - Keep it under 80 words.
                - Focus only on the main idea.
                """;

            case DETAILED -> """
                - Generate a detailed summary in 6-10 sentences.
                - Include all important concepts.
                - Preserve technical details where relevant.
                - Explain the content clearly.
                """;

            case BULLET_POINTS -> """
                - Summarize the note using bullet points.
                - Use 5-10 bullet points.
                - Each bullet should contain one important idea.
                - Keep bullets short and easy to read.
                - Each bullet should start with '-'.
                - Each bullet should contain one key idea.
                - Do not write paragraphs.
                """;
            };

            return """
            You are an assistant that summarizes notes.

            Given the following note:

            Title: %s
            Content: %s

            Instructions:
            %s

            - Generate 3-5 relevant tags.
            - Tags should be short keywords.
            - Do not repeat the title.
            - Return ONLY valid JSON.

            {
              "summary": "your summary here",
              "tags": ["tag1", "tag2", "tag3"]
            }
            """.formatted(title, content, summaryInstruction);
    }
    public String chooseModel(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
//            return "nvidia/nemotron-nano-12b-v2-vl:free";
            return "nex-agi/nex-n2-pro:free";
        } else {
            return "nvidia/nemotron-3-super-120b-a12b:free";
        }
    }
    public Map<String, Object> buildRequest(String prompt, String imageUrl) {

        Map<String, Object> request = new HashMap<>();
        request.put("model", chooseModel(imageUrl));

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        List<Object> content = new ArrayList<>();

        // text part (always present)
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("type", "text");
        textPart.put("text", prompt);
        content.add(textPart);

        // image part (only if exists)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Map<String, Object> image = new HashMap<>();
            image.put("type", "image_url");

            Map<String, Object> imageData = new HashMap<>();
            imageData.put("url", imageUrl);

            image.put("image_url", imageData);
            content.add(image);
        }

        message.put("content", content);
        request.put("messages", List.of(message));

        return request;
    }
}
