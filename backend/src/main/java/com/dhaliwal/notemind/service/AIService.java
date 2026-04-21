package com.dhaliwal.notemind.service;

public interface AIService {
    String getSummaryFromTextGenAI(String title, String content);
    String getSummary(String title, String content, String imageUrl);
}
