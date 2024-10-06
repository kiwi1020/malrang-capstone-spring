package com.malrang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.dto.ChatDto;
import com.malrang.dto.RatingDto;
import com.malrang.dto.UserDto;
import com.malrang.entity.User;
import com.malrang.repository.UserRepository;
import com.malrang.service.FriendListService;
import com.malrang.service.UserDetailService;
import com.malrang.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailService userDetailService;
    private UserService userService;
    private FriendListService friendListService;
    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userDetailService = Mockito.mock(UserDetailService.class);
        userService = Mockito.mock(UserService.class);
        friendListService = Mockito.mock(FriendListService.class);

        testUser = User.builder()
                .email("user@gmail.com")
                .password("test")
                .nickname("user")
                .language("Korean")
                .averageRating(0.0)
                .chatRoom(null)
                .build();

        userRepository.deleteAll();
        userRepository.save(testUser);

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
    }

    @DisplayName("userInfo: 사용자의 정보를 반환한다.")
    @Test
    void userInfo() throws Exception {
        // Given
        final String url = "/user/info";

        // When, Then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("user@gmail.com"))) // 인증된 사용자 설정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("user@gmail.com"))
                .andExpect(jsonPath("$.userName").value(testUser.getNickname()))
                .andExpect(jsonPath("$.userLanguages").value(testUser.getLanguage()))
                .andExpect(jsonPath("$.userRating").value(testUser.getAverageRating()));
    }

    @DisplayName("updateUserInfo: 사용자 정보를 업데이트한다.")
    @Test
    void updateUserInfo() throws Exception {
        // Given
        final String url = "/user/info/update";
        UserDto.UpdateUserRequest request = new UserDto.UpdateUserRequest("user@gmail.com","Updated User", "English");
        final String requestBody = objectMapper.writeValueAsString(request);

        // When, Then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.nickname").value(request.getNickname()))
                .andExpect(jsonPath("$.language").value(request.getLanguage()));
    }


    @DisplayName("logout: 로그아웃한다.")
    @Test
    void logout() throws Exception {
        // Given
        final String url = "/user/logout";

        // When, Then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("user@gmail.com"))) // 인증된 사용자 설정
                .andExpect(status().isOk());
    }
}
