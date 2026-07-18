package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.entity.type.SummaryType;

public interface AIService {
    String getSummaryFromTextGenAI(String title, String content);
    AINoteResponse getAIResponse(
            String title,
            String content,
            String imageUrl
    );
    AINoteResponse getAIResponse(
            String title,
            String content,
            String imageUrl,
            SummaryType summaryType
    );
}
