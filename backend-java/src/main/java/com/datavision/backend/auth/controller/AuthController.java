package com.datavision.backend.auth.controller;

import com.datavision.backend.auth.service.IAuthService;
import com.datavision.backend.auth.dto.LoginResponseDto;
import com.datavision.backend.auth.dto.LoginUserDto;
import com.datavision.backend.auth.dto.RegisterUserDto;
import com.datavision.backend.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterUserDto registerUserDto){
        UserDto response = authService.register(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginUserDto input){
        LoginResponseDto response = authService.login(input);
        return ResponseEntity.ok(response);
    }
}
