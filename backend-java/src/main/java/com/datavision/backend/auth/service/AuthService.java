package com.datavision.backend.auth.service;

import com.datavision.backend.common.dto.user.LoginResponseDto;
import com.datavision.backend.common.dto.user.LoginUserDto;
import com.datavision.backend.common.dto.user.RegisterUserDto;
import com.datavision.backend.common.dto.user.UserDto;
import com.datavision.backend.common.exceptions.UserAlreadyExistsException;
import com.datavision.backend.common.exceptions.UserNotFoundException;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserDto register(RegisterUserDto registerUserDto){
        log.info("Registering user: {}", registerUserDto.getUsername());
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            log.warn("Username {} already exists.", registerUserDto.getUsername());
            throw new UserAlreadyExistsException("Username already exists.");
        }
        User newUser = new User();
        newUser.setUsername(registerUserDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        User savedUser = userRepository.save(newUser);
        return new UserDto(savedUser);
    }

    public LoginResponseDto login(LoginUserDto input){
        log.info("Authenticating user {}", input.getUsername());
        Optional<User> user = userRepository.findByUsername(input.getUsername());
        if (user.isPresent()){
            User userToAuthenticate = user.get();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
            LoginResponseDto response = createLoginResponse(userToAuthenticate);
            log.info("User {} logged in successfully", input.getUsername());
            return response;
        }else{
            log.warn("User {} not found", input.getUsername());
            throw new UserNotFoundException("User not found");
        }
    }

    private LoginResponseDto createLoginResponse(User user){
        String token = jwtService.generateToken(user);
        Long expirationTime = jwtService.getExpirationTime();
        return new LoginResponseDto(token, expirationTime);
    }

}
