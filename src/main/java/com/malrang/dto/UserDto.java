package com.malrang.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDto {
    @Getter
    public static class UpdateUserRequest {
        private String email;
        private String nickname;
        private String language;
        private String languageLevel;
    }
}
