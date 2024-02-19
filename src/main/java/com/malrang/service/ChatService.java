package com.malrang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.dto.ChatDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatDto.ChatRoom> chatRooms;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    public List<ChatDto.ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatDto.ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    public ChatDto.ChatRoom createRoom(String name) {
        String randomId = UUID.randomUUID().toString();
        ChatDto.ChatRoom chatRoom = ChatDto.ChatRoom.builder()
                .roomId(randomId)
                .name(name)
                .build();
        chatRooms.put(randomId, chatRoom);
        return chatRoom;
    }
}