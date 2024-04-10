package com.malrang.controller;


import com.malrang.configuration.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.malrang.configuration.oauth.OAuth2SuccessHandler;
import com.malrang.dto.UserDto;
import com.malrang.repository.RefreshTokenRepository;
import com.malrang.service.UserService;
import com.malrang.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final RefreshTokenRepository refreshTokenRepository;

    /*@GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Test");
        CookieUtil.deleteCookie(request, response, "refresh_token");
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }*/

}