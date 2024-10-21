package com.malrang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.configuration.support.TestContainerSupport;
import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class WebSockChatHandlerTest extends TestContainerSupport {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChatService chatService;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private WebSockChatHandler webSockChatHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자가 입장 메시지를 보낼 때, 세션이 추가되고 메시지가 전송된다.")
    void handleTextMessage_whenEnterMessage_shouldAddSessionAndBroadcastMessage() throws Exception {
        // Given
        ChatDto.ChatMessage chatMessage = new ChatDto.ChatMessage(ChatDto.ChatMessage.MessageType.ENTER,
                "test-room-id", "user1", null);

        Set<WebSocketSession> sessions = new HashSet<>();

        ChatDto.ChatRoom chatRoom = new ChatDto.ChatRoom("test-room-id", "Test Room",
                "Korean", "Beginner", 0L,
                sessions);

        String payload = new ObjectMapper().writeValueAsString(chatMessage);
        TextMessage textMessage = new TextMessage(payload);


        // When
        when(chatService.findRoomById("test-room-id")).thenReturn(chatRoom);
        when(objectMapper.readValue(anyString(), eq(ChatDto.ChatMessage.class))).thenReturn(chatMessage);
        when(objectMapper.writeValueAsString(any(ChatDto.ChatMessage.class))).thenReturn("{\"type\":\"ENTER\",\"roomId\":\"test-room-id\",\"sender\":\"user1\",\"message\":null}");

        // Then
        webSockChatHandler.handleTextMessage(session, textMessage);
        assertTrue(chatRoom.getSessions().contains(session));
    }

    @Test
    @DisplayName("사용자가 퇴장 메시지를 보낼 때, 세션이 제거되고 메시지가 전송된다.")
    void handleTextMessage_whenQuitMessage_shouldRemoveSessionAndBroadcastMessage() throws Exception {
        // Given
        ChatDto.ChatMessage chatMessage = new ChatDto.ChatMessage(ChatDto.ChatMessage.MessageType.QUIT,
                "test-room-id", "user1", null);

        Set<WebSocketSession> sessions = new HashSet<>();

        ChatDto.ChatRoom chatRoom = new ChatDto.ChatRoom("test-room-id", "Test Room",
                "Korean", "Beginner", 0L,
                sessions);

        String payload = new ObjectMapper().writeValueAsString(chatMessage);
        TextMessage textMessage = new TextMessage(payload);


        // When
        when(chatService.findRoomById("test-room-id")).thenReturn(chatRoom);
        when(objectMapper.readValue(anyString(), eq(ChatDto.ChatMessage.class))).thenReturn(chatMessage);
        when(objectMapper.writeValueAsString(any(ChatDto.ChatMessage.class))).thenReturn("{\"type\":\"ENTER\",\"roomId\":\"test-room-id\",\"sender\":\"user1\",\"message\":null}");


        // Then
        webSockChatHandler.handleTextMessage(session, textMessage);
        assertFalse(chatRoom.getSessions().contains(session));
    }

    @Test
    @DisplayName("일반 메시지를 보낼 때, 방의 모든 세션으로 메시지가 전송된다.")
    void handleTextMessage_whenNormalMessage_shouldBroadcastMessageToAllSessions() throws Exception {
        // Given
        ChatDto.ChatMessage chatMessage = new ChatDto.ChatMessage(ChatDto.ChatMessage.MessageType.ENTER,
                "test-room-id", "user1", "test message");

        Set<WebSocketSession> sessions = new HashSet<>();

        ChatDto.ChatRoom chatRoom = new ChatDto.ChatRoom("test-room-id", "Test Room",
                "Korean", "Beginner", 0L,
                sessions);

        String payload = new ObjectMapper().writeValueAsString(chatMessage);
        TextMessage textMessage = new TextMessage(payload);

        // When
        when(chatService.findRoomById("test-room-id")).thenReturn(chatRoom);
        when(objectMapper.readValue(anyString(), eq(ChatDto.ChatMessage.class))).thenReturn(chatMessage);
        when(objectMapper.writeValueAsString(any(ChatDto.ChatMessage.class))) .thenReturn("{\"type\":\"MESSAGE\",\"roomId\":\"test-room-id\",\"sender\":\"user1\",\"message\":\"test message\"}");
        when(session.isOpen()).thenReturn(true);

        // Then
        webSockChatHandler.handleTextMessage(session, textMessage);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    @DisplayName("세션이 종료되면 방이 삭제된다.")
    void afterConnectionClosed_shouldDeleteRoom() throws Exception {
        // When
        webSockChatHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Then
        verify(chatService, times(1)).deleteRoom();
    }
}
