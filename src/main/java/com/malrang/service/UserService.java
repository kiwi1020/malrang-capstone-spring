package com.malrang.service;

import com.malrang.dto.RatingDto;
import com.malrang.dto.UserDto;
import com.malrang.entity.FriendList;
import com.malrang.entity.User;
import com.malrang.entity.UserRating;
import com.malrang.repository.UserRatingRepository;
import com.malrang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRatingRepository userRatingRepository;

    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입
    @Transactional
    public User update(UserDto.UpdateUserRequest dto) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            // 기존 사용자가 존재할 경우에만 업데이트를 수행합니다.
            User existingUser = optionalUser.get();

            // 기존 사용자의 필드를 업데이트합니다.
            existingUser.setNickname(dto.getNickname());
            existingUser.setLanguage(dto.getLanguage());
            existingUser.setInterest(dto.getInterest());

            // 업데이트된 사용자를 저장합니다.
            userRepository.save(existingUser);
            return existingUser;
        } else {
            // 기존 사용자가 존재하지 않는 경우에는 적절한 예외를 발생시킵니다.
            throw new Exception("User not found");
        }
    }
    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    @Transactional
    public List<UserDto.getAllUserResponse> searchUsers(String language, String userEmail) {
        // 모든 사용자를 가져와서 언어 필터링
        List<User> allUsers = userRepository.findAll(); // 모든 사용자 조회

        FriendList userFriendList = (FriendList) redisTemplate.opsForHash().get("friendList", userEmail);
        assert userFriendList != null;
        Set<String> friendEmails = userFriendList.getFriendStatuses().keySet();
        Set<String> requestEmails = userFriendList.getSentRequests();

        if (language.equals("All"))
            return allUsers.stream()
                    .filter(user -> !user.getEmail().equalsIgnoreCase(userEmail)) // 현재 사용자의 이메일 제외
                    .filter(user -> !friendEmails.contains(user.getEmail())) // 친구가 아닌 사용자만 필터링
                    .filter(user -> !requestEmails.contains(user.getEmail())) // 친구 요청을 보낸 사용자가 아닌 사용자만
                    .map(user -> new UserDto.getAllUserResponse(
                            user.getEmail(),
                            user.getLanguage(),
                            user.getAverageRating(), // averageRating은 User 엔티티에 있어야 함
                            user.getInterest()
                    ))
                    .collect(Collectors.toList());

        else
            return allUsers.stream()
                    .filter(// 현재 사용자의 이메일 제외
                            user -> !user.getEmail().equalsIgnoreCase(userEmail) && user.getLanguage().equalsIgnoreCase(language)) // 언어 필터링
                    .filter(user -> !friendEmails.contains(user.getEmail())) // 친구가 아닌 사용자만 필터링
                    .filter(user -> !requestEmails.contains(user.getEmail())) // 친구 요청을 보낸 사용자가 아닌 사용자만
                    .map(user -> new UserDto.getAllUserResponse(
                            user.getEmail(),
                            user.getLanguage(),
                            user.getAverageRating(), // averageRating은 User 엔티티에 있어야 함
                            user.getInterest()
                    ))
                    .collect(Collectors.toList());
    }

    @Transactional
    public RatingDto.RatingResponse rateUser(String ratedUserEmail, String raterUserEmail, Double rating) {
        User ratedUser = userRepository.findByEmail(ratedUserEmail)
                .orElseThrow(() -> new RuntimeException("Rated user not found"));
        User raterUser = userRepository.findByEmail(raterUserEmail)
                .orElseThrow(() -> new RuntimeException("Rater user not found"));

        UserRating userRating = UserRating.builder()
                .ratedUser(ratedUser)
                .raterUser(raterUser)
                .rating(rating)
                .build();

        userRatingRepository.save(userRating);

        ratedUser.updateAverageRating();
        userRepository.save(ratedUser);

        RatingDto.RatingResponse ratingResponse = new RatingDto.RatingResponse(ratedUserEmail, raterUserEmail, rating);
        return ratingResponse;
    }
}