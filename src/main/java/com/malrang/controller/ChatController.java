package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

//restAPI 방식으로 바꿀 것
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @RequestMapping("/chat/chatList")
    public String chatList(Model model) {
        List<ChatDto.ChatRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "chatList";
    }

    @GetMapping("/chat/chatList/filterRooms")
    public String chatListByFilter(Model model,
                                   @RequestParam(value = "roomName", required = false) String roomName,
                                   @RequestParam(value = "roomLanguage", required = false) String roomLanguage,
                                   @RequestParam(value = "roomLanguageLevel", required = false) String roomLanguageLevel) {
        List<ChatDto.ChatRoom> roomList = chatService.searchChatRoomsByFilter(roomName, roomLanguage, roomLanguageLevel);
        model.addAttribute("roomList", roomList);
        return "chatList";
    }

    @GetMapping("/chat/chatRoom")
    public String chatRoom() {
        return "chatRoom";
    }


    /*@GetMapping("/chat/chatRoom")
    public String chatRoom(Model model, @RequestParam String roomId){
        ChatDto.ChatRoom room = chatService.findRoomById(roomId);
        model.addAttribute("room",room);   //현재 방에 들어오기위해서 필요한데...... 접속자 수 등등은 실시간으로 보여줘야 돼서 여기서는 못함
        return "chatRoom";
    }*/
}