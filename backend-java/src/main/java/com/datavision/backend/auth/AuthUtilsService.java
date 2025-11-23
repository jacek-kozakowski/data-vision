package com.datavision.backend.auth;

import com.datavision.backend.common.exceptions.UserNotFoundException;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUtilsService {
    private final UserRepository userRepository;

    public User getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Did not find user with username: " + username));
    }
}
