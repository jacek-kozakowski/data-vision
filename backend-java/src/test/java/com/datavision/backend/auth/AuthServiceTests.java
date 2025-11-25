package com.datavision.backend.auth;

import com.datavision.backend.auth.service.AuthService;
import com.datavision.backend.auth.jwt.JwtService;
import com.datavision.backend.auth.dto.RegisterUserDto;
import com.datavision.backend.user.dto.UserDto;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    public void registerTest(){
        RegisterUserDto input = new RegisterUserDto();
        input.setUsername("test");
        input.setPassword("test1234567");

        UserDto response = new UserDto();
        response.setId(1L);
        response.setUsername("test");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = authService.register(input);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(result.getId(), savedUser.getId());
        assertEquals(result.getUsername(), input.getUsername());
        assertEquals(result.getUsername(), savedUser.getUsername());
        assertEquals(savedUser.getId(), result.getId());

    }
}
