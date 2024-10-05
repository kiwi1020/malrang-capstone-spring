package com.malrang.service;

import com.malrang.dto.UserDto;
import com.malrang.entity.User;
import com.malrang.entity.UserRating;
import com.malrang.repository.UserRatingRepository;
import com.malrang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRatingRepository userRatingRepository;
    @Transactional
    public User update(UserDto.UpdateUserRequest dto) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            // 기존 사용자가 존재할 경우에만 업데이트를 수행합니다.
            User existingUser = optionalUser.get();

            // 기존 사용자의 필드를 업데이트합니다.
            existingUser.setNickname(dto.getNickname());
            existingUser.setLanguage(dto.getLanguage());

            // 업데이트된 사용자를 저장합니다.
            return userRepository.save(existingUser);
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
    public User rateUser(String ratedUserEmail, String raterUserEmail, Double rating) {
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

        return ratedUser;
    }
}