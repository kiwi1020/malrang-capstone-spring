package com.malrang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CreateAccessTokenDto {
    @Getter
    @Setter
    public static class CreateAccessTokenRequest {
        private String refreshToken;
    }

    @AllArgsConstructor
    @Getter
    public static class CreateAccessTokenResponse {
        private String accessToken;
    }
}
