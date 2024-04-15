package com.malrang.dto;

import com.malrang.entity.Article;
import com.malrang.entity.ChatRoom;
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
        private String roomLanguage;
        private String roomLanguageLevel;

        private Set<WebSocketSession> sessions = new HashSet<>();

        @Builder
        public ChatRoom(String roomId, String roomName, String roomLanguage, String roomLanguageLevel) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.roomLanguage = roomLanguage;
            this.roomLanguageLevel = roomLanguageLevel;
        }
    }
    @Getter
    @NoArgsConstructor
    public static class CreateRoom {
        String roomName;
        String roomLanguage;
        String roomLanguageLevel;
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
