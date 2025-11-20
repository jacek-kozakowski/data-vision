package com.datavision.backend.auth;

import com.datavision.backend.user.model.User;
import com.datavision.backend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    private static UserRepository userRepository;

    public AuthUtils(UserRepository userRepository) {
        AuthUtils.userRepository = userRepository;
    }

    public static User getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username."));
    }
}
