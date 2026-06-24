package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.AINoteResponse;

public interface AIService {
    String getSummaryFromTextGenAI(String title, String content);
    AINoteResponse getAIResponse(String title, String content, String imageUrl);
}
