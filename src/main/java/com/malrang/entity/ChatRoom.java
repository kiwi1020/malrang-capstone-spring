package com.malrang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "roomId", nullable = false, unique = true)
    private String roomId;

    @Column(name = "roomName")
    private String roomName;

    @Column(name = "roomLanguage")
    private String roomLanguage;

    @Column(name = "roomLevel")
    private String roomLevel;

    @Column(name = "roomHeadCount")
    private Long roomHeadCount;

    public void setRoomHeadCount(Long newHeadCount) {
        this.roomHeadCount = newHeadCount;
    }

    @Builder
    public ChatRoom(String roomId, String roomName, String roomLanguage, String roomLevel, Long roomHeadCount) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomLanguage = roomLanguage;
        this.roomLevel = roomLevel;
        this.roomHeadCount = roomHeadCount;
    }
}