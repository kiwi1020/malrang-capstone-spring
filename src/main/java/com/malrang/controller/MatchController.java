package com.malrang.controller;

import com.malrang.dto.MatchDto;
import com.malrang.service.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private String sessionId = null;
    @Autowired
    private MatchService matchService;

    // tag :: async
    @GetMapping("/chat/join")
    @ResponseBody
    public DeferredResult<MatchDto.MatchResponse> joinRequest(HttpServletRequest request) {
        sessionId = request.getSession().getId();
        logger.info(">> Join request. session id : {}", sessionId);

        final MatchDto.MatchRequest user = new MatchDto.MatchRequest(sessionId);
        final DeferredResult<MatchDto.MatchResponse> deferredResult = new DeferredResult<>(null);
        matchService.joinChatRoom(user, deferredResult);

        deferredResult.onCompletion(() -> matchService.cancelChatRoom(user));
        deferredResult.onError((throwable) -> matchService.cancelChatRoom(user));
        deferredResult.onTimeout(() -> matchService.timeout(user));

        return deferredResult;
    }

    @GetMapping("/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelRequest() {
        logger.info(">> Cancel request. session id : {}", sessionId);

        final MatchDto.MatchRequest user = new MatchDto.MatchRequest(sessionId);
        matchService.cancelChatRoom(user);

        return ResponseEntity.ok().build();
    }

    // -- tag :: async

    // tag :: websocket stomp
//    @MessageMapping("/chat.message/{chatRoomId}")
//    public void sendMessage(@DestinationVariable("chatRoomId") String chatRoomId, @Payload ChatMessage chatMessage) {
//        logger.info("Request message. roomd id : {} | chat message : {} | principal : {}", chatRoomId, chatMessage);
//        if (!StringUtils.hasText(chatRoomId) || chatMessage == null) {
//            return;
//        }
//
//        if (chatMessage.getMessageType() == MessageType.CHAT) {
//            chatService.sendMessage(chatRoomId, chatMessage);
//        }
//    }
    // -- tag :: websocket stomp
}