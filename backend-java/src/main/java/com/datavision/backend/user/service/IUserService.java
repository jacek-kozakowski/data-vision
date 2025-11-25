package com.datavision.backend.user.service;

import com.datavision.backend.user.dto.UserDto;
import com.datavision.backend.user.model.User;

public interface IUserService {
    UserDto getUserDtoById(Long id);
    UserDto getUserDtoByUsername(String username);
    User getUserByUsername(String username);
}
