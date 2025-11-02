package com.datavision.backend.common.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Long expirationTime;
}
