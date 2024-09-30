package com.malrang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malrang.entity.FriendList;
import com.malrang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FriendListService {

    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper; // ObjectMapper 주입

    // 친구 목록 생성
    public FriendList createFriendList(String email) {
        FriendList friendList = new FriendList(email, new HashMap<>(), new HashSet<>(), new HashSet<>());
        redisTemplate.opsForHash().put("friendList", email, friendList);
        return friendList;
    }

    // 친구 요청 보내기
    public FriendList sendFriendRequest(String email, String friendEmail) {
         userRepository.findByEmail(friendEmail)
                 .orElseThrow(() -> new IllegalArgumentException("Friend with email " + friendEmail + " not found"));
        // 요청 보낼 사용자 FriendList 가져오기
        FriendList senderFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", email);
        if (senderFriendList != null) {
            senderFriendList.getSentRequests().add(friendEmail);
            redisTemplate.opsForHash().put("friendList", email, senderFriendList);
        }
        // 요청 받을 사용자 FriendList 가져오기
        FriendList receiverFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", friendEmail);
        if (receiverFriendList != null) {
            receiverFriendList.getReceivedRequests().add(email);
            redisTemplate.opsForHash().put("friendList", friendEmail, receiverFriendList);
        }
        return senderFriendList;
    }

    // 친구 요청 수락하기
    public FriendList acceptFriendRequest(String email, String friendEmail) {
        // 요청 받은 사용자 FriendList 가져오기
        FriendList senderFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", email);
        if (senderFriendList != null) {
            senderFriendList.getReceivedRequests().remove(friendEmail);
            senderFriendList.getFriendStatuses().put(friendEmail, true);
            redisTemplate.opsForHash().put("friendList", email, senderFriendList);
        }
        // 요청 보낸 사용자 FriendList 가져오기
        FriendList receiverFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", friendEmail);
        if (receiverFriendList != null) {
            receiverFriendList.getSentRequests().remove(email);
            receiverFriendList.getFriendStatuses().put(email, true);
            redisTemplate.opsForHash().put("friendList", friendEmail, receiverFriendList);
        }
        return senderFriendList;
    }

    // 친구 요청 거부하기
    public FriendList rejectFriendRequest(String email, String friendEmail) {
        // 요청 받은 사용자 FriendList 가져오기
        FriendList senderFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", email);
        if (senderFriendList != null) {
            senderFriendList.getReceivedRequests().remove(friendEmail);
            redisTemplate.opsForHash().put("friendList", email, senderFriendList);
        }
        // 요청 보낸 사용자 FriendList 가져오기
        FriendList receiverFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", friendEmail);
        if (receiverFriendList != null) {
            receiverFriendList.getSentRequests().remove(email);
            redisTemplate.opsForHash().put("friendList", friendEmail, receiverFriendList);
        }
        return senderFriendList;
    }
    // 친구 목록 조회
    public FriendList getFriendList(String email) {
        return (FriendList) redisTemplate.opsForHash().get("friendList", email);
    }

    // 유저의 상태 업데이트
    public void updateUserStatus(String email, Boolean status) {
        Map<Object, Object> allFriendLists = redisTemplate.opsForHash().entries("friendList");

        // 모든 사용자에 대해
        for (Map.Entry<Object, Object> entry : allFriendLists.entrySet()) {
            FriendList friendList = (FriendList) entry.getValue();

            // 만약 해당 사용자의 friendStatuses에 업데이트된 email이 있으면
            if (friendList.getFriendStatuses().containsKey(email)) {
                // 그 사용자의 friendStatuses에서 해당 친구(email)의 상태를 업데이트
                friendList.getFriendStatuses().put(email, status);

                // 변경된 FriendList를 다시 Redis에 저장
                redisTemplate.opsForHash().put("friendList", friendList.getEmail(), friendList);
            }
        }
    }
}
