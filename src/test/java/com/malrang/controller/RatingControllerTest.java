package com.malrang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.dto.ChatDto;
import com.malrang.dto.RatingDto;
import com.malrang.entity.User;
import com.malrang.repository.UserRatingRepository;
import com.malrang.repository.UserRepository;
import com.malrang.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRatingRepository userRatingRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("user@gmail.com")
                .password("test")
                .nickname("user")
                .language("Korean")
                .averageRating(0.0)
                .chatRoom(null)
                .build();

        User friend = User.builder()
                .email("friend@gmail.com")
                .password("test")
                .nickname("friend")
                .language("Korean")
                .averageRating(0.0)
                .chatRoom(null)
                .build();

        userRepository.deleteAll();
        userRepository.save(user);
        userRepository.save(friend);
    }

    @AfterEach
    void tearDown() {
        // 테스트가 끝난 후 리포지터리를 삭제
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 평점을 성공적으로 등록한다")
    void rateUser_success() throws Exception {
        // given
        final String url = "/rating/rateUser";
        final RatingDto.RatingRequest dto = new RatingDto.RatingRequest("user@gmail.com", "friend@gmail.com", 4.5);
        final String requestBody = objectMapper.writeValueAsString(dto);

        // When & Then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }
}
