package com.malrang.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDto {
    @Setter
    @Getter
    public class AddUserRequest {
        private String email;
        private String password;
    }
}
