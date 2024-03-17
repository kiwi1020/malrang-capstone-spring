package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

//restAPI 방식으로 바꿀 것
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @RequestMapping("/chat/chatList")
    public String chatList(Model model){
        List<ChatDto.ChatRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList",roomList);
        return "chatList";
    }


    /*@PostMapping("/chat/createRoom")  //방을 만들었으면 해당 방으로 가야지.
    public String createRoom(Model model, @RequestParam String roomName, Principal principal) {
        ChatDto.ChatRoom room = chatService.createRoom(roomName);
        model.addAttribute("room",room);
        System.out.println(principal.getName());
        model.addAttribute("username",principal.getName());
        return "chatRoom";  //만든사람이 채팅방 1빠로 들어가게 됩니다
    }
*/
    /*@GetMapping("/chat/chatRoom")
    public String chatRoom(Model model, @RequestParam String roomId){
        ChatDto.ChatRoom room = chatService.findRoomById(roomId);
        model.addAttribute("room",room);   //현재 방에 들어오기위해서 필요한데...... 접속자 수 등등은 실시간으로 보여줘야 돼서 여기서는 못함
        return "chatRoom";
    }*/
}