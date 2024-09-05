package com.malrang.controller;

import com.malrang.service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChatService chatService;

    @Test
    public void testChatList() {
        // Mock chatService response
        when(chatService.findAllRoom()).thenReturn(Collections.emptyList());

        webTestClient.get().uri("/chat/chatList")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    assertNotNull(responseBody, "Response Body is null");

                    // Perform HTML validations
                    assert responseBody.contains("<title>");  // Example: Check for presence of <title> tag
                });
    }

    @Test
    public void testChatListByFilter() {
        // Mock chatService response with filters
        when(chatService.searchChatRoomsByFilter(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/chat/chatList/filterRooms")
                        .queryParam("roomName", "testRoom")
                        .queryParam("roomLanguage", "EN")
                        .queryParam("roomLanguageLevel", "Advanced")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    assertNotNull(responseBody, "Response Body is null");

                    // Perform HTML validations
                    assert responseBody.contains("<title>");  // Example: Check for presence of <title> tag
                });
    }

    @Test
    public void testChatRoom() {
        webTestClient.get().uri("/chat/chatRoom")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    assertNotNull(responseBody, "Response Body is null");

                    // Perform HTML validations
                    assert responseBody.contains("<title>");  // Example: Check for presence of <title> tag
                });
    }
}
