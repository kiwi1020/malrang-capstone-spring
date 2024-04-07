package com.malrang.service;

import com.malrang.dto.UserDto;
import com.malrang.entity.User;
import com.malrang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User update(UserDto.UpdateUserRequest dto) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            // 기존 사용자가 존재할 경우에만 업데이트를 수행합니다.
            User existingUser = optionalUser.get();

            // 기존 사용자의 필드를 업데이트합니다.
            existingUser.setNickname(dto.getNickname());
            existingUser.setLanguage(dto.getLanguage());
            existingUser.setLanguageLevel(dto.getLanguageLevel());

            // 업데이트된 사용자를 저장합니다.
            return userRepository.save(existingUser);
        } else {
            // 기존 사용자가 존재하지 않는 경우에는 적절한 예외를 발생시킵니다.
            throw new Exception("User not found");
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}