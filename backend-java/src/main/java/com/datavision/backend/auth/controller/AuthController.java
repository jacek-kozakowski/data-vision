package com.datavision.backend.auth.controller;

import com.datavision.backend.auth.service.AuthService;
import com.datavision.backend.common.dto.user.LoginResponseDto;
import com.datavision.backend.common.dto.user.LoginUserDto;
import com.datavision.backend.common.dto.user.RegisterUserDto;
import com.datavision.backend.common.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Validated RegisterUserDto registerUserDto){
        UserDto response = authService.register(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Validated LoginUserDto input){
        LoginResponseDto response = authService.login(input);
        return ResponseEntity.ok(response);
    }
}
