package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.dhaliwal.notemind.entity.type.SummaryType.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AIServiceImplTest {

    @Autowired
    AIService aiService;

    @Test
    void getSummaryFromTextGenAI() {
        // This method of AIService is not used
    }

    @Test
    void getAIResponse() {
        AINoteResponse response = aiService.getAIResponse("this is title", "make random summary", null);
        System.out.println(response);
        assert(response!=null);
    }
    @Test
    void getDifferentTypeOfSummary(){
        AINoteResponse shortSummary = aiService.getAIResponse(
                "Introduction to Spring Boot",
                "Spring Boot is a Java framework that simplifies backend development" +
                        " by providing auto-configuration, embedded servers, and starter dependencies. " +
                        "It helps developers build REST APIs quickly with minimal configuration.",
                null,
                SHORT
        );

        AINoteResponse detailedSummary = aiService.getAIResponse(
                "Machine Learning Basics",
                "Machine learning is a branch of artificial intelligence that " +
                        "enables computers to learn patterns from data. It includes " +
                        "supervised, unsupervised, and reinforcement learning. Machine " +
                        "learning is widely used in recommendation systems, fraud detection, " +
                        "image recognition, natural language processing, and predictive analytics.",
                null,
                DETAILED
        );

        AINoteResponse bulletPointSummary = aiService.getAIResponse(
                "Git Essentials",
                "Git is a distributed version control system used to track changes in source code. It allows developers to create branches, merge changes, collaborate with teams, maintain project history, and manage software versions efficiently.",
                null,
                BULLET_POINTS
        );
        assert(shortSummary.getSummary()!=null);
        assert(detailedSummary.getSummary()!=null);
        assert(bulletPointSummary.getSummary()!=null);
    }
}