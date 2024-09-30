package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//restAPI 방식으로 바꿀 것
@Controller
@RequiredArgsConstructor
public class ChatViewController {
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

}