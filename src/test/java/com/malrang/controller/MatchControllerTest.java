package com.malrang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.configuration.support.TestContainerSupport;
import com.malrang.dto.MatchDto;
import com.malrang.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class MatchControllerTest extends TestContainerSupport {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private MatchController matchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("채팅방 매칭 참가 요청 테스트")
    void testJoinRequest() throws Exception {
        // given
        String sessionId = "test-session-id";
        String language = "Korean";

        //when & then
        mockMvc.perform(get("/chat/join")
                        .sessionAttr("sessionId", sessionId)  // Mock 세션 ID 설정
                        .param("language", language)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("채팅방 매칭 취소 요청 테스트")
    void testCancelRequest() throws Exception {
        // given
        String sessionId = "test-session-id";
        String language = "Korean";

        //when & then
        mockMvc.perform(get("/cancel")
                        .sessionAttr("sessionId", sessionId)  // Mock 세션 ID 설정
                        .param("language", language)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
