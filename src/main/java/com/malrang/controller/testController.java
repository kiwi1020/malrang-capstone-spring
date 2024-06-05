package com.malrang.controller;

import com.malrang.dto.ChatDto;
import com.malrang.dto.RatingDto;
import com.malrang.dto.UserDto;
import com.malrang.entity.ChatRoom;
import com.malrang.entity.User;
import com.malrang.service.ChatService;
import com.malrang.service.UserDetailService;
import com.malrang.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class testController {
    private final ChatService chatService;
    private final UserDetailService userDetailService;
    private final UserService userService;

    @PostMapping("/chat/createRoom")  //방을 만들었으면 해당 방으로 가야지.
    public ResponseEntity createRoom(@RequestBody ChatDto.CreateRoom roomData, Principal principal) {

        ChatDto.ChatRoom room = chatService.createRoom(roomData.getRoomName(), roomData.getRoomLanguage(), roomData.getRoomLanguageLevel(), 0L);;
        chatService.addChatRoom(room.getRoomId(), roomData.getRoomName(), roomData.getRoomLanguage(), roomData.getRoomLanguageLevel(), 0L);

        Map<String, Object> response = new HashMap<>();
        response.put("roomId",room.getRoomId());
        return ResponseEntity.ok(response);  //만든사람이 채팅방 1빠로 들어가게 됩니다
    }

    @PostMapping("/userInfo")
    public ResponseEntity userInfo(Principal principal) {
        if (principal == null) {
            // 401 Unauthorized 상태 코드를 반환하도록 함
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        String userEmail = principal.getName();
        User user = userDetailService.loadUserByUsername(userEmail);
        Map<String, Object> response = new HashMap<>();
        response.put("userEmail", userEmail);
        response.put("userName", user.getNickname());
        response.put("userLanguages", user.getLanguage());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/userInfo/update")
    public ResponseEntity updateUserInfo(@RequestBody UserDto.UpdateUserRequest dto) throws Exception {
        userService.update(dto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/chat/setHeadCount")
    public ResponseEntity setHeadCount(@RequestBody ChatDto.addHeadCount dto, Principal principal) throws Exception {
        String userEmail = principal.getName();
        chatService.setHeadCount(dto.getRoomId(), dto.getStatus(), userEmail);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("chat/getParticipants")
    public ResponseEntity<List<String>> getParticipants(@RequestParam String roomId) throws Exception {
        // roomId를 사용하여 참가자 목록을 가져옵니다.
        List<String> participants = chatService.getParticipants(roomId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping("chat/rateUser")
    public ResponseEntity rateUser(@RequestBody RatingDto.RatingRequest request) throws Exception {
        userService.rateUser(request.getRatedUserEmail(), request.getRaterUserEmail(), request.getRating());
        return new ResponseEntity(HttpStatus.OK);
    }
}
