package com.malrang.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

public class ChatDto {

    @Getter
    public static class ChatRoom {
        private String roomId;
        private String roomName;
        private Set<WebSocketSession> sessions = new HashSet<>();

        @Builder
        public ChatRoom(String roomId, String roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
        }
    }
    @Getter
    @NoArgsConstructor
    public static class CreateRoom {
        String roomName;
    }


    @Getter
    @Setter
    public static class ChatMessage {
        // 메시지 타입 : 입장, 채팅, 나감
        public enum MessageType {
            ENTER, TALK, QUIT
        }

        private MessageType type; // 메시지 타입
        private String roomId; // 방번호
        private String sender; // 메시지 보낸사람
        private String message; // 메시지
    }


}
