package com.malrang.service;

import com.malrang.entity.FriendList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class FriendListService {

    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

    // 친구 목록 생성
    public FriendList createFriendList(String email) {
        FriendList friendList = new FriendList(email, new HashSet<>(), new HashSet<>(), new HashSet<>());
        redisTemplate.opsForHash().put("friendList", email, friendList);
        return friendList;
    }

    // 친구 요청 보내기
    public FriendList sendFriendRequest(String email, String friendEmail) {
        FriendList friendList = (FriendList) redisTemplate.opsForHash().get("friendList", email);
        if (friendList == null) {
            friendList = new FriendList(email, new HashSet<>(), new HashSet<>(), new HashSet<>());
        }
        friendList.getSentRequests().add(friendEmail);
        redisTemplate.opsForHash().put("friendList", email, friendList);
        return friendList;
    }

    // 친구 요청 수락하기
    public FriendList acceptFriendRequest(String email, String friendEmail) {
        FriendList friendList = (FriendList) redisTemplate.opsForHash().get("friendList", email);
        if (friendList != null) {
            friendList.getReceivedRequests().remove(friendEmail);
            friendList.getFriends().add(friendEmail);
            redisTemplate.opsForHash().put("friendList", email, friendList);
        }
        return friendList;
    }

    // 친구 요청 거부하기
    public FriendList rejectFriendRequest(String email, String friendEmail) {
        FriendList friendList = (FriendList) redisTemplate.opsForHash().get("friendList", email);
        if (friendList != null) {
            friendList.getReceivedRequests().remove(friendEmail);
            redisTemplate.opsForHash().put("friendList", email, friendList);
        }
        return friendList;
    }

    // 친구 목록 조회
    public FriendList getFriendList(String email) {
        return (FriendList) redisTemplate.opsForHash().get("friendList", email);
    }

}
