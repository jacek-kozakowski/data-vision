package com.datavision.backend.user.service;

import com.datavision.backend.common.exceptions.UserNotFoundException;
import com.datavision.backend.common.dto.user.UserDto;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserDto getUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Invalid id."));
        return new UserDto(user);
    }
    public UserDto getUserDtoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username."));
        return new UserDto(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username."));
    }




}
