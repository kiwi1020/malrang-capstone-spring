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
        userService.update(dto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity rateUser(@RequestBody RatingDto.RatingRequest request) throws Exception {
        userService.rateUser(request.getRatedUserEmail(), request.getRaterUserEmail(), request.getRating());
        return new ResponseEntity(HttpStatus.OK);
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
}
