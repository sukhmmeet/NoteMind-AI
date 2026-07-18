package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}