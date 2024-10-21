package com.malrang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.configuration.support.TestContainerSupport;
import com.malrang.dto.FriendListDto;
import com.malrang.entity.ChatRoom;
import com.malrang.entity.FriendList;
import com.malrang.entity.User;
import com.malrang.repository.UserRepository;
import com.malrang.service.FriendListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class FriendListControllerTest extends TestContainerSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

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

        User friend = User.builder()
                .email("friend@gmail.com")
                .password("test")
                .nickname("friend")
                .language("Korean")
                .averageRating(null)
                .chatRoom(null)
                .build();

        userRepository.deleteAll();
        userRepository.save(user);
        userRepository.save(friend);

        // Redis에 FriendList 초기화
        FriendList userFriendList = new FriendList();
        userFriendList.setEmail(user.getEmail());
        userFriendList.setFriendStatuses(new HashMap<>());
        userFriendList.setSentRequests(new HashSet<>()); // 초기화
        userFriendList.setReceivedRequests(new HashSet<>()); // 초기화

        redisTemplate.opsForHash().put("friendList", user.getEmail(), userFriendList); // user의 FriendList 저장

        FriendList friendFriendList = new FriendList();
        friendFriendList.setEmail(friend.getEmail());
        friendFriendList.setFriendStatuses(new HashMap<>());
        friendFriendList.setSentRequests(new HashSet<>()); // 초기화
        friendFriendList.setReceivedRequests(new HashSet<>()); // 초기화

        redisTemplate.opsForHash().put("friendList", friend.getEmail(), friendFriendList); // friend의 FriendList 저장

        // Set up security context for the authenticated user
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("user@gmail.com");

        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        // Redis에서 user와 friend의 FriendList 삭제
        redisTemplate.opsForHash().delete("friendList", "user@gmail.com");
        redisTemplate.opsForHash().delete("friendList", "friend@gmail.com");
        // 테스트가 끝난 후 리포지터리를 삭제
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("친구 목록 생성")
    void testCreateFriendList() throws Exception {
        String userEmail = "user@gmail.com";

        mockMvc.perform(post("/api/friend/create")
                .with(user(userEmail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail)); // JSON 응답에 대한 검증
    }

    @Test
    @DisplayName("친구 요청 보내기")
    void testSendFriendRequest() throws Exception {
        String userEmail = "user@gmail.com";
        String friendEmail = "friend@gmail.com";
        FriendListDto.FriendRequestDto dto = new FriendListDto.FriendRequestDto(friendEmail);
        final String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/friend/request/send")
                .with(user(userEmail)) // Mock Principal 설정
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail)); // JSON 응답에 대한 검증
    }

    @Test
    @DisplayName("친구 요청 수락하기")
    void testAcceptFriendRequest() throws Exception {
        String userEmail = "user@gmail.com";
        String friendEmail = "friend@gmail.com";
        FriendListDto.FriendRequestDto dto = new FriendListDto.FriendRequestDto(friendEmail);
        final String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/friend/request/accept")
                        .with(user(userEmail)) // Mock Principal 설정
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail)) // 이메일 검증
                .andExpect(jsonPath("$.friendStatuses['" + friendEmail + "']").value(true)); // friendStatuses에 friendEmail 확인
    }

    @Test
    @DisplayName("친구 요청 거부하기")
    void testRejectFriendRequest() throws Exception {
        String userEmail = "user@gmail.com";
        String friendEmail = "friend@gmail.com";
        FriendListDto.FriendRequestDto dto = new FriendListDto.FriendRequestDto(friendEmail);
        final String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/friend/request/reject")
                        .with(user(userEmail)) // Mock Principal 설정
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail)); // 이메일 검증
    }

    @Test
    @DisplayName("친구 목록 조회하기")
    void testGetFriendList() throws Exception {
        String userEmail = "user@gmail.com";

        mockMvc.perform(get("/api/friend/request/getList")
                        .with(user(userEmail))) // Mock Principal 설정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail)); // 이메일 검증
    }
}
