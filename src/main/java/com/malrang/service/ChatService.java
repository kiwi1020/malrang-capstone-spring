package com.malrang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.dto.ChatDto;
import com.malrang.entity.ChatRoom;
import com.malrang.entity.User;
import com.malrang.repository.ChatRoomRepository;
import com.malrang.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private Map<String, ChatDto.ChatRoom> chatRooms;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

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
    public void addChatRoom(String roomId, String roomName, String roomLanguage, String roomLevel, Long roomHeadCount) {

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

    @Transactional
    public void deleteRoom() {
        // 방의 상태를 로그에 기록하여 확인합니다.
        log.info("Starting room deletion process.");

        // 삭제할 방의 ID 리스트를 가져옵니다.
        List<String> roomsToDelete = chatRooms.values().stream()
                .peek(room -> log.info("Checking room ID {} with sessions: {}", room.getRoomId(), room.getSessions()))
                .filter(room -> room.getSessions().isEmpty())
                .map(ChatDto.ChatRoom::getRoomId)
                .toList();

        // 삭제할 방이 있는지 로그를 추가하여 확인합니다.
        log.info("Rooms to delete: {}", roomsToDelete);

        for (String roomId : roomsToDelete) {
            // 1. 채팅방 찾기
            ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId).orElse(null);
            if (chatRoom != null) {
                // 2. 채팅방에 소속된 유저의 chatRoomId를 null로 설정
                List<User> usersInRoom = userRepository.findByChatRoom(chatRoom);
                for (User user : usersInRoom) {
                    user.setChatRoom(null);
                    userRepository.save(user);  // 변경 사항 저장
                }

                // 3. 채팅방 삭제
                chatRooms.remove(roomId);
                chatRoomRepository.delete(chatRoom);
            }
        }
    }


    public List<ChatDto.ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll().stream()
                .map(chat -> new ChatDto.ChatRoom(chat.getRoomId(), chat.getRoomName(), chat.getRoomLanguage(), chat.getRoomLevel(), chat.getRoomHeadCount()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ChatDto.ChatRoom> searchChatRoomsByFilter(String roomName, String roomLanguage, String roomLevel) {

        String name = (roomName == null) ? "" : roomName;
        String language = (roomLanguage == null) ? "" : roomLanguage;
        String level = (roomLevel == null) ? "" : roomLevel;

        return chatRoomRepository.findByRoomNameContainingAndRoomLanguageContainingAndRoomLevelContaining(name, language, level).stream()
                .map(chat -> new ChatDto.ChatRoom(chat.getRoomId(), chat.getRoomName(), chat.getRoomLanguage(), chat.getRoomLevel(), chat.getRoomHeadCount()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void setHeadCount(String roomId, String status, String userEmail) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId).orElseThrow(() -> new Exception("room doesn't exist"));

        // 방을 찾지 못한 경우 예외 처리
        if (chatRoom == null) {
            throw new RuntimeException("Room not found with ID: " + roomId);
        }

        // 현재 인원수를 가져와서 1을 증가시킵니다.
        Long currentHeadCount = (chatRoom.getRoomHeadCount());
        Long newHeadCount = status.equals("join") ? currentHeadCount + 1 : currentHeadCount - 1;

        // 새로운 인원수를 저장합니다.
        chatRoom.setRoomHeadCount(newHeadCount);

        // 사용자를 추가 또는 제거합니다.
        if (status.equals("join")) {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new Exception("User doesn't exist"));
            chatRoom.addUser(user);
        }
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public List<String> getParticipants(String roomId) throws Exception {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseGet(() -> {
                    log.warn("Chat room with ID {} does not exist.", roomId);
                    return null;
                });

        List<String> emails = new ArrayList<>();
        for (User user : chatRoom.getUsers()) {
            emails.add(user.getEmail());
        }
        return emails;
    }
}