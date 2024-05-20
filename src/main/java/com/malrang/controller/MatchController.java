package com.malrang.controller;
import com.malrang.dto.MatchDto;
import com.malrang.service.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.security.Principal;

@RestController
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private MatchService matchService;

    @GetMapping("/chat/join")
    @ResponseBody
    public DeferredResult<MatchDto.MatchResponse> joinRequest(HttpServletRequest request, @RequestParam String language) {
        String sessionId = request.getSession().getId(); // 현재 요청의 세션 ID 가져오기
        logger.info(">> Join request. session id : {}", sessionId);

        final MatchDto.MatchRequest user = new MatchDto.MatchRequest(sessionId);
        final DeferredResult<MatchDto.MatchResponse> deferredResult = new DeferredResult<>(null);
        matchService.joinChatRoom(user, deferredResult, language);

        deferredResult.onCompletion(() -> matchService.cancelChatRoom(user, language));
        deferredResult.onError((throwable) -> matchService.cancelChatRoom(user, language));
        deferredResult.onTimeout(() -> matchService.timeout(user, language));

        return deferredResult;
    }

    @GetMapping("/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelRequest(HttpServletRequest request, @RequestParam String language) {
        String sessionId = request.getSession().getId(); // 현재 요청의 세션 ID 가져오기
        logger.info(">> Cancel request. session id : {}", sessionId);

        final MatchDto.MatchRequest user = new MatchDto.MatchRequest(sessionId);
        matchService.cancelChatRoom(user, language);

        return ResponseEntity.ok().build();
    }

}