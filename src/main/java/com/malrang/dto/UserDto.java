package com.malrang.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDto {
    @Getter
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String email;
        private String nickname;
        private String language;
    }
}
