package com.datavision.backend.user.controller;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.user.dto.UserDto;
import com.datavision.backend.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final AuthUtilsService authUtils;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getAuthenticatedUser(){
        UserDto response = userService.getUserDtoById(authUtils.getAuthenticatedUser().getId());
        return ResponseEntity.ok(response);
    }
}
