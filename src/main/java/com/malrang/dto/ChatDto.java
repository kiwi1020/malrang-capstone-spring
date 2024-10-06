package com.malrang.dto;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

public class ChatDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoom {
        private String roomId;
        private String roomName;
        private String roomLanguage;
        private String roomLanguageLevel;
        private Long roomHeadCount;

        private Set<WebSocketSession> sessions = new HashSet<>();

        @Builder
        public ChatRoom(String roomId, String roomName, String roomLanguage, String roomLanguageLevel, Long roomHeadCount) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.roomLanguage = roomLanguage;
            this.roomLanguageLevel = roomLanguageLevel;
            this.roomHeadCount = roomHeadCount;
        }
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRoom {
        String roomName;
        String roomLanguage;
        String roomLanguageLevel;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class addHeadCount {
        String roomId;
        String status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
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
