package com.datavision.backend.user.controller;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.common.dto.user.UserDto;
import com.datavision.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthUtilsService authUtils;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getAuthenticatedUser(){
        UserDto response = userService.getUserDtoById(authUtils.getAuthenticatedUser().getId());
        return ResponseEntity.ok(response);
    }
}
