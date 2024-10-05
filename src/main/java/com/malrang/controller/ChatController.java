package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/createRoom")
    public ResponseEntity createRoom(@RequestBody ChatDto.CreateRoom roomData ) {
        ChatDto.ChatRoom room = chatService.createRoom(roomData.getRoomName(), roomData.getRoomLanguage(), roomData.getRoomLanguageLevel(), 0L);
        chatService.addChatRoom(room.getRoomId(), roomData.getRoomName(), roomData.getRoomLanguage(), roomData.getRoomLanguageLevel(), 0L);

        Map<String, Object> response = new HashMap<>();
        response.put("roomId", room.getRoomId());
        response.put("roomName", room.getRoomName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/setHeadCount")
    public ResponseEntity setHeadCount(@RequestBody ChatDto.addHeadCount dto, Principal principal) throws Exception {
        String userEmail = principal.getName();
        chatService.setHeadCount(dto.getRoomId(), dto.getStatus(), userEmail);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/getParticipants")
    public ResponseEntity<List<String>> getParticipants(@RequestParam String roomId) throws Exception {
        List<String> participants = chatService.getParticipants(roomId);
        return ResponseEntity.ok(participants);
    }
}