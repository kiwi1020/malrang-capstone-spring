package com.malrang.controller;

import com.malrang.dto.FriendListDto;
import com.malrang.entity.FriendList;
import com.malrang.service.FriendListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendListController {

    private final FriendListService friendListService;

    // 친구 목록 생성
    @PostMapping("/create")
    public ResponseEntity<FriendList> createFriendList(Principal principal) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.createFriendList(userEmail));
    }

    // 친구 요청 보내기
    @PostMapping("/request/send")
    public ResponseEntity<FriendList> sendFriendRequest(Principal principal, @RequestBody FriendListDto.FriendRequestDto dto) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.sendFriendRequest(userEmail, dto.getFriendEmail()));
    }

    // 친구 요청 수락하기
    @PostMapping("/request/accept")
    public ResponseEntity<FriendList> acceptFriendRequest(Principal principal, @RequestBody FriendListDto.FriendRequestDto dto) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.acceptFriendRequest(userEmail, dto.getFriendEmail()));
    }

    // 친구 요청 거부하기
    @PostMapping("/request/reject")
    public ResponseEntity<FriendList> rejectFriendRequest(Principal principal, @RequestBody FriendListDto.FriendRequestDto dto) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.rejectFriendRequest(userEmail, dto.getFriendEmail()));
    }

    // 친구 목록 조회하기
    @GetMapping("/request/getList")
    public ResponseEntity<FriendList> getFriendList(Principal principal) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.getFriendList(userEmail));
    }

    // 채팅방에 친구 초대하기
    @PostMapping("/request/invite")
    public ResponseEntity<FriendList> inviteFriendRequest(Principal principal, @RequestBody FriendListDto.InviteRequestDto dto) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.inviteFriendRequest(userEmail, dto.getFriendEmail(), dto.getRoomId()));
    }

    @PostMapping("/response/invite")
    public ResponseEntity<FriendList> inviteFriendResponse(Principal principal, @RequestBody FriendListDto.InviteRequestDto dto) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(friendListService.inviteFriendResponse(userEmail, dto.getFriendEmail(), dto.getRoomId()));
    }

}
