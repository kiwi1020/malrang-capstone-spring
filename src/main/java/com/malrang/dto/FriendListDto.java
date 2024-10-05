package com.malrang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FriendListDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendRequestDto {
        private String friendEmail;
    }
}