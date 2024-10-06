package com.malrang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RatingDto {

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class RatingRequest {
        private String ratedUserEmail;
        private String raterUserEmail;
        private Double rating;
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class RatingResponse {
        private String ratedUserEmail;
        private String raterUserEmail;
        private Double rating;
    }
}
