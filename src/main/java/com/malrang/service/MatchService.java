package com.malrang.service;

import com.malrang.dto.ChatDto;
import com.malrang.dto.MatchDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@RequiredArgsConstructor
public class MatchService {
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    private Map<String, Map<MatchDto.MatchRequest, DeferredResult<MatchDto.MatchResponse>>> waitingUsersMap;
    private Map<String, String> connectedUsers;
    private ReentrantReadWriteLock lock;
    private final ChatService chatService;

    @PostConstruct
    private void setUp() {
        this.waitingUsersMap = new ConcurrentHashMap<>();
        this.waitingUsersMap.put("korean", new LinkedHashMap<>());
        this.waitingUsersMap.put("english", new LinkedHashMap<>());
        this.waitingUsersMap.put("chinese", new LinkedHashMap<>());
        this.waitingUsersMap.put("spanish", new LinkedHashMap<>());
        this.waitingUsersMap.put("french", new LinkedHashMap<>());
        this.waitingUsersMap.put("japanese", new LinkedHashMap<>());
        this.lock = new ReentrantReadWriteLock();
        this.connectedUsers = new ConcurrentHashMap<>();
    }

    @Async("asyncThreadPool")
    public void joinChatRoom(MatchDto.MatchRequest request, DeferredResult<MatchDto.MatchResponse> deferredResult, String language) {
        logger.info("## Join chat room request. {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());
        if (request == null || deferredResult == null) {
            return;
        }

        try {
            lock.writeLock().lock();
            // 언어별 대기열에 요청 및 결과 추가
            Map<MatchDto.MatchRequest, DeferredResult<MatchDto.MatchResponse>> waitingUsers = waitingUsersMap.computeIfAbsent(language, k -> new LinkedHashMap<>());
            waitingUsers.put(request, deferredResult);
        } finally {
            lock.writeLock().unlock();
            establishChatRoom(language);
        }
    }

    public void cancelChatRoom(MatchDto.MatchRequest chatRequest, String language) {
        try {
            lock.writeLock().lock();
            // 언어별 대기열에서 요청 제거
            Map<MatchDto.MatchRequest, DeferredResult<MatchDto.MatchResponse>> waitingUsers = waitingUsersMap.get(language);
            if (waitingUsers != null) {
                setJoinResult(waitingUsers.remove(chatRequest), new MatchDto.MatchResponse(MatchDto.MatchResponse.ResponseResult.CANCEL, chatRequest.getSessionId(), null));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void establishChatRoom(String language) {
        try {
            logger.debug("Current waiting users : " + waitingUsersMap.size());
            lock.readLock().lock();
            Map<MatchDto.MatchRequest, DeferredResult<MatchDto.MatchResponse>> waitingUsers = waitingUsersMap.get(language);

            if (waitingUsers != null && waitingUsers.size() >= 2) {
                Iterator<MatchDto.MatchRequest> itr = waitingUsers.keySet().iterator();
                MatchDto.MatchRequest user1 = itr.next();
                MatchDto.MatchRequest user2 = itr.next();

                DeferredResult<MatchDto.MatchResponse> user1Result = waitingUsers.remove(user1);
                DeferredResult<MatchDto.MatchResponse> user2Result = waitingUsers.remove(user2);

                ChatDto.ChatRoom room = chatService.createRoom("Random ChatRoom", language, "beginner", 0L);

                user1Result.setResult(new MatchDto.MatchResponse(MatchDto.MatchResponse.ResponseResult.SUCCESS, user1.getSessionId(), room.getRoomId()));
                user2Result.setResult(new MatchDto.MatchResponse(MatchDto.MatchResponse.ResponseResult.SUCCESS, user2.getSessionId(), room.getRoomId()));

                chatService.addChatRoom(room.getRoomId(), "Random ChatRoom", language, "beginner", 0L);
            }
        } catch (Exception e) {
            logger.warn("Exception occur while checking waiting users", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void setJoinResult(DeferredResult<MatchDto.MatchResponse> result, MatchDto.MatchResponse response) {
        if (result != null) {
            result.setResult(response);
        }
    }

    public void timeout(MatchDto.MatchRequest chatRequest, String language) {
        try {
            lock.writeLock().lock();
            Map<MatchDto.MatchRequest, DeferredResult<MatchDto.MatchResponse>> waitingUsers = waitingUsersMap.get(language);
            if (waitingUsers != null) {
                setJoinResult(waitingUsers.remove(chatRequest), new MatchDto.MatchResponse(MatchDto.MatchResponse.ResponseResult.TIMEOUT, chatRequest.getSessionId(), null));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}