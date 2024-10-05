package com.malrang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.dto.ChatDto;
import com.malrang.entity.ChatRoom;
import com.malrang.entity.User;
import com.malrang.repository.ChatRoomRepository;
import com.malrang.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("user@gmail.com")
                .password("test")
                .nickname("user")
                .language("Korean")
                .averageRating(null)
                .chatRoom(null)
                .build();

        userRepository.deleteAll();
        userRepository.save(user);

        //Mocking 채팅방을 생성
        ChatRoom mockChatRoom = ChatRoom.builder()
                .roomId("test-room-id")
                .roomName("Test Room")
                .roomLanguage("Korean")
                .roomLevel("Beginner")
                .roomHeadCount(0L)
                .users(null)
                .build();

        chatRoomRepository.deleteAll();
        chatRoomRepository.save(mockChatRoom);

        // Set up security context for the authenticated user
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("user@gmail.com");

        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        // 테스트가 끝난 후 리포지터리를 삭제
        userRepository.deleteAll();
        chatRoomRepository.deleteAll();
    }

    @DisplayName("createRoom: 채팅방 생성에 성공한다.")
    @Test
    public void testCreateRoom() throws Exception {
        // given
        final String url = "/chat/createRoom";
        final ChatDto.CreateRoom roomData = new ChatDto.CreateRoom("Test Room", "Korean", "Beginner");
        final String requestBody = objectMapper.writeValueAsString(roomData);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .with(user("user@gmail.com"))); // 인증된 사용자 설정

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName").value("Test Room")); // roomName으로 검증
    }
    @DisplayName("setHeadCount: 채팅방의 인원 수를 설정하는데 성공한다.")
    @Test
    public void testSetHeadCount() throws Exception {
        // given
        final String url = "/chat/setHeadCount";
        final ChatDto.addHeadCount headCountData = new ChatDto.addHeadCount("test-room-id", "join");
        final String requestBody = objectMapper.writeValueAsString(headCountData);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)
                .with(user("user@gmail.com"))); // 인증된 사용자 설정

        // then
        result.andExpect(status().isOk());
    }

    @DisplayName("getParticipants: 채팅방 참가자 목록을 반환한다.")
    @Test
    public void testGetParticipants() throws Exception {
        // given
        final String url = "/chat/getParticipants?roomId=test-room-id";

        // when
        ResultActions result = mockMvc.perform(get(url)
                .with(user("user@gmail.com"))); // 인증된 사용자 설정

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
