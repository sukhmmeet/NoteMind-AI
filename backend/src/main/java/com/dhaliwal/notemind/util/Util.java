package com.dhaliwal.notemind.util;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class Util {
    public String getPrompt(String title, String content){
        return "You are an assistant that summarizes notes.\n" +
                "Given the following note:\n\n" +
                "Title: " + title + "\n" +
                "Content: " + content + "\n\n" +
                "Instructions:\n" +
                "- Generate a concise summary in 2-3 sentences.\n" +
                "- Capture the key idea and important details.\n" +
                "- If an image is provided, use it only if it adds meaningful context.\n" +
                "- Do not describe the image unless it is relevant.\n" +
                "- Do not repeat the title.\n" +
                "- Keep it clear and simple.\n\n" +
                "Summary:";
    }
    public String chooseModel(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return "nvidia/nemotron-nano-12b-v2-vl:free";
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
