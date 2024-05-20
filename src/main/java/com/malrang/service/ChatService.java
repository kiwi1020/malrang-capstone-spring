package com.malrang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.dto.ChatDto;
import com.malrang.entity.ChatRoom;
import com.malrang.repository.ChatRoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private Map<String, ChatDto.ChatRoom> chatRooms;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

   /* public List<ChatDto.ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }*/

    public ChatDto.ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    @Transactional
    public ChatDto.ChatRoom createRoom(String roomName, String roomLanguage, String roomLanguageLevel, Long roomHeadCount) {
        String randomId = UUID.randomUUID().toString();
        ChatDto.ChatRoom chatRoom = ChatDto.ChatRoom.builder()
                .roomId(randomId)
                .roomName(roomName)
                .roomLanguage(roomLanguage)
                .roomLanguageLevel(roomLanguageLevel)
                .roomHeadCount(roomHeadCount)
                .build();
        chatRooms.put(randomId, chatRoom);
        return chatRoom;
    }

    @Transactional
    public void addChatRoom(String roomId, String roomName, String roomLanguage, String roomLevel, Long roomHeadCount)  {

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByRoomId(roomId);
        if (existingRoom.isPresent()) {
            return;
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomName(roomName)
                .roomLanguage(roomLanguage)
                .roomLevel(roomLevel)
                .roomHeadCount(roomHeadCount)
                .build();
        chatRoomRepository.save(chatRoom);

    }
    public List<ChatDto.ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll().stream()
                .map(chat -> new ChatDto.ChatRoom(chat.getRoomId(), chat.getRoomName(),chat.getRoomLanguage(), chat.getRoomLevel(), chat.getRoomHeadCount() ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ChatDto.ChatRoom> searchChatRoomsByFilter(String roomName, String roomLanguage, String roomLevel) {

        String name = (roomName == null) ? "" : roomName;
        String language = (roomLanguage == null) ? "" : roomLanguage;
        String level = (roomLevel == null) ? "" : roomLevel;

        return chatRoomRepository.findByRoomNameContainingAndRoomLanguageContainingAndRoomLevelContaining(name, language, level).stream()
                .map(chat -> new ChatDto.ChatRoom(chat.getRoomId(), chat.getRoomName(),chat.getRoomLanguage(), chat.getRoomLevel() ,chat.getRoomHeadCount()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void setHeadCount(String roomId, String status) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(() -> new Exception("room doesn't exist"));

        // 방을 찾지 못한 경우 예외 처리
        if (chatRoom == null) {
            throw new RuntimeException("Room not found with ID: " + roomId);
        }

        // 현재 인원수를 가져와서 1을 증가시킵니다.
        Long currentHeadCount = (chatRoom.getRoomHeadCount());
        Long newHeadCount = status.equals("join") ?  currentHeadCount + 1 : currentHeadCount - 1;

        // 새로운 인원수를 저장합니다.
        chatRoom.setRoomHeadCount(newHeadCount);

        // 변경사항을 저장합니다.
        chatRoomRepository.save(chatRoom);
    }

}