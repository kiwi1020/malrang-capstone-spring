package com.malrang.dto;

import lombok.Getter;

public class FriendListDto {

    @Getter
    public static class FriendRequestDto {
        private String friendEmail;
    }
}