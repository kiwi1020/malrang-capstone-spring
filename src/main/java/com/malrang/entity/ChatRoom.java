package com.malrang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // 수정 필요
    private List<User> users = new ArrayList<>();

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

    // 유틸리티 메서드 추가
    public void addUser(User user) {
        users.add(user);
        user.setChatRoom(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setChatRoom(null);
    }
}