package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class testController {
    private final ChatService chatService;

    @PostMapping("/chat/createRoom")  //방을 만들었으면 해당 방으로 가야지.
    public ResponseEntity createRoom(@RequestBody String roomName, Principal principal) {
        ChatDto.ChatRoom room = chatService.createRoom(roomName);;
        String name = principal.getName();
        Map<ChatDto.ChatRoom, String> map = new HashMap<>();
        map.put(room,name);
        return ResponseEntity.ok(map);  //만든사람이 채팅방 1빠로 들어가게 됩니다
    }

    @GetMapping("/chat/chatRoom")
    public String chatRoom(Model model, @RequestParam String roomId){
        ChatDto.ChatRoom room = chatService.findRoomById(roomId);
        model.addAttribute("room",room);   //현재 방에 들어오기위해서 필요한데...... 접속자 수 등등은 실시간으로 보여줘야 돼서 여기서는 못함
        return "chatRoom";
    }
}
