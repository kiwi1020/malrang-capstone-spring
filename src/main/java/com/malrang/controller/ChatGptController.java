package com.malrang.controller;

import com.malrang.dto.GptDto;
import com.malrang.service.ChatGptService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/chat-gpt")
@RestController
public class ChatGptController {
    private final ChatGptService chatGptService;
    private static final Logger logger = LoggerFactory.getLogger(ChatGptController.class);
    @Operation(summary = "Question to Chat-GPT")
    @PostMapping("/question")
    public ResponseEntity<Map<String, Object>> sendQuestion(
            @RequestBody GptDto.QuestionRequest questionRequest) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            GptDto.ChatGptResponse chatGptResponse = chatGptService.askQuestion(questionRequest);
            String answer = chatGptResponse.getChoices().get(0).getMessage().getContent();
            logger.info("Answer from Chat-GPT: {}", answer);

            // JSON 형식으로 데이터를 반환
            responseData.put("answer", answer);
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            String code = e.getMessage();
            logger.error("Error occurred: {}", code);
            responseData.put("error", code);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }
    }
}
