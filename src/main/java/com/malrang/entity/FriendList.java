package com.malrang.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Set;

@RedisHash(value = "friendList")
public class FriendList {

    @Id
    @Getter
    private String email; // 사용자의 이메일

    @Indexed
    @Getter
    private Set<String> friends; // 친구 목록 (친구로 추가된 사용자들의 ID)

    @Indexed
    @Getter
    private Set<String> sentRequests; // 내가 보낸 친구 요청 목록

    @Indexed
    @Getter
    private Set<String> receivedRequests; // 내가 받은 친구 요청 목록

    public FriendList() {
    }

    public FriendList(String email, Set<String> friends, Set<String> sentRequests, Set<String> receivedRequests) {
        this.email = email;
        this.friends = friends;
        this.sentRequests = sentRequests;
        this.receivedRequests = receivedRequests;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFriends(Set<String> friends) {
        this.friends = friends;
    }

    public void setSentRequests(Set<String> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public void setReceivedRequests(Set<String> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }
}
