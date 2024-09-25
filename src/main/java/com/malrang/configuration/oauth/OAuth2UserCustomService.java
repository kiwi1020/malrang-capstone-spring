package com.malrang.configuration.oauth;

import com.malrang.entity.FriendList;
import com.malrang.entity.User;
import com.malrang.repository.UserRepository;
import com.malrang.service.FriendListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final FriendListService friendListService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest); // ❶ 요청을 바탕으로 유저 정보를 담은 객체 반환
        saveOrUpdate(user);

        return user;
    }

    // ❷ 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user;

        // 사용자 정보를 DB에서 조회하고, 없으면 생성
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            // 기존 사용자 업데이트
            user = optionalUser.get().update(name);
        } else {
            // 새 사용자 생성
            user = User.builder()
                    .email(email)
                    .nickname(name)
                    .language("korean")
                    .averageRating(0.0)
                    .build();

            // 친구 목록을 Redis에 생성
            friendListService.createFriendList(email);
        }

        // 사용자 저장
        return userRepository.save(user);
    }
}
