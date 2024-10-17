package com.malrang.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RedisHash(value = "friendList")
public class FriendList {

    @Id
    @Getter
    private String email; // 사용자의 이메일

    @Indexed
    @Getter
    private Map<String, Boolean> friendStatuses; // 친구 이메일과 상태를 매핑

    @Indexed
    @Getter
    private Set<String> sentRequests; // 내가 보낸 친구 요청 목록

    @Indexed
    @Getter
    private Set<String> receivedRequests; // 내가 받은 친구 요청 목록

    @Indexed
    @Getter
    private Map<String, String> receivedInvites; // 내가 받은 친구 요청 목록

    public FriendList() {

    }
    public FriendList(String email, Map<String, Boolean> friendStatuses, Set<String> sentRequests, Set<String> receivedRequests, Map<String, String> receivedInvites) {
        this.email = email;
        this.friendStatuses = friendStatuses;
        this.sentRequests = sentRequests;
        this.receivedRequests = receivedRequests;
        this.receivedInvites = receivedInvites;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFriendStatuses(Map<String, Boolean> friendStatuses) {
        this.friendStatuses = friendStatuses;
    }

    public void setSentRequests(Set<String> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public void setReceivedRequests(Set<String> receivedRequests) { this.receivedRequests = receivedRequests; }

    public void setReceivedInvites(Map<String, String> receivedInvites) { this.receivedInvites = receivedInvites; }

}