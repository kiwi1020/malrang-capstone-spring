package com.malrang.controller;

import com.malrang.dto.RatingDto;
import com.malrang.dto.UserDto;
import com.malrang.entity.User;
import com.malrang.service.FriendListService;
import com.malrang.service.UserDetailService;
import com.malrang.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserDetailService userDetailService;
    private final UserService userService;
    private final FriendListService friendListService;

    @PostMapping("/info")
    public ResponseEntity userInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        String userEmail = principal.getName();
        User user = userDetailService.loadUserByUsername(userEmail);
        Map<String, Object> response = new HashMap<>();
        response.put("userEmail", userEmail);
        response.put("userName", user.getNickname());
        response.put("userLanguages", user.getLanguage());
        response.put("userRating", user.getAverageRating());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/info/update")
    public ResponseEntity updateUserInfo(@RequestBody UserDto.UpdateUserRequest dto) throws Exception {
        User user = userService.update(dto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal, HttpServletResponse httpServletResponse) {
        if (principal != null) {
            String email = principal.getName();
            friendListService.updateUserStatus(email, false);
        }
        // 리프레시 토큰 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(0);  // 쿠키 만료 시간 0으로 설정
        refreshTokenCookie.setPath("/");  // 모든 경로에서 유효하도록 설정
        httpServletResponse.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/searchUsers")
    public ResponseEntity<List<UserDto.getAllUserResponse>> searchUsers(@RequestBody UserDto.getAllUserRequest dto, Principal principal) {
        String language;
        // dto.getLanguage()가 null일 경우를 체크
        if (dto.getLanguage() == null || dto.getLanguage().isEmpty()) {
            language = "All"; // 기본값 설정
        } else {
            language = dto.getLanguage();
        }

        List<UserDto.getAllUserResponse> users = userService.searchUsers(language, principal.getName());
        return ResponseEntity.ok(users);
    }
}
