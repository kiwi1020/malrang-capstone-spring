package com.malrang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateAccessTokenDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAccessTokenRequest {
        private String refreshToken;
    }

    @AllArgsConstructor
    @Getter
    public static class CreateAccessTokenResponse {
        private String accessToken;
    }
}
