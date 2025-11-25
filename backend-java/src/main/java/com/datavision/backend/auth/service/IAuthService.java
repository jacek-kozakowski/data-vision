package com.datavision.backend.auth.service;

import com.datavision.backend.auth.dto.LoginResponseDto;
import com.datavision.backend.auth.dto.LoginUserDto;
import com.datavision.backend.auth.dto.RegisterUserDto;
import com.datavision.backend.user.dto.UserDto;

public interface IAuthService {

    UserDto register(RegisterUserDto registerUserDto);
    LoginResponseDto login(LoginUserDto input);
}
