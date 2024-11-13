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
        private String interest;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class getAllUserRequest {
        private String language;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class getAllUserResponse {
        private String email;
        private String language;
        private double averageRating;
        private String interest;
    }

}
