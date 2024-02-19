package com.malrang.controller;

import com.malrang.dto.GptDto;
import com.malrang.service.ChatGptService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/chat-gpt")
@RestController
public class ChatGptController {
    private final ChatGptService chatGptService;

    @Operation(summary = "Question to Chat-GPT")
    @PostMapping("/question")
    public ResponseEntity sendQuestion(
            @RequestBody GptDto.QuestionRequest questionRequest) {
        String code = null;
        GptDto.ChatGptResponse chatGptResponse = null;
        try {
            chatGptResponse = chatGptService.askQuestion(questionRequest);
        } catch (Exception e) {
            code = e.getMessage();
        }
        //return 부분은 자유롭게 수정하시면됩니다. ex)return chatGptResponse;
        return ResponseEntity.ok(chatGptResponse != null ? chatGptResponse.getChoices().get(0).getMessage().getContent() : new GptDto.ChatGptResponse());
    }
}
