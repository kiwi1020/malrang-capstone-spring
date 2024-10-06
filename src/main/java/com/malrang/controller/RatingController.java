package com.malrang.controller;

import com.malrang.dto.RatingDto;
import com.malrang.entity.User;
import com.malrang.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rating")
public class RatingController {
    private final UserService userService;

    @PostMapping("/rateUser")
    public ResponseEntity rateUser(@RequestBody RatingDto.RatingRequest request) throws Exception {
        RatingDto.RatingResponse ratedUser = userService.rateUser(request.getRatedUserEmail(), request.getRaterUserEmail(), request.getRating());
        return ResponseEntity.ok(ratedUser);
    }
}
