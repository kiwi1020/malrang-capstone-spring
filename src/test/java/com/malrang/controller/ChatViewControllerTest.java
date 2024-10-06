package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChatViewControllerTest {
    @Mock
    private ChatService chatService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @DisplayName("채팅방 목록 조회 성공")
    @Test
    public void testChatList() throws Exception {
        List<ChatDto.ChatRoom> roomList = Collections.emptyList();
        Mockito.when(chatService.findAllRoom()).thenReturn(roomList);

        mockMvc.perform(get("/chat/chatList"))
                .andExpect(status().isOk())
                .andExpect(view().name("chatList"))
                .andExpect(model().attribute("roomList", roomList));
    }

    @DisplayName("필터로 채팅방 목록 조회 성공")
    @Test
    public void testChatListByFilter() throws Exception {
        List<ChatDto.ChatRoom> filteredRoomList = Collections.emptyList();
        Mockito.when(chatService.searchChatRoomsByFilter(anyString(), anyString(), anyString())).thenReturn(filteredRoomList);

        mockMvc.perform(get("/chat/chatList/filterRooms")
                        .param("roomName", "Test Room")
                        .param("roomLanguage", "English")
                        .param("roomLanguageLevel", "Intermediate"))
                .andExpect(status().isOk())
                .andExpect(view().name("chatList"))
                .andExpect(model().attribute("roomList", filteredRoomList));
    }

    @DisplayName("채팅방 페이지 조회 성공")
    @Test
    public void testChatRoom() throws Exception {
        mockMvc.perform(get("/chat/chatRoom"))
                .andExpect(status().isOk())
                .andExpect(view().name("chatRoom"));
    }
}
