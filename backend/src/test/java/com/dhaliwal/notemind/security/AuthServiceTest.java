package com.dhaliwal.notemind.security;

import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.security.dto.RequestDto;
import com.dhaliwal.notemind.security.dto.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private AuthService authService;

    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new RequestDto();
        requestDto.setUsername("john");
        requestDto.setPassword("password123");
    }

    @Test
    void signup_ShouldCreateUserSuccessfully() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(authUtil.generateAccessToken(savedUser))
                .thenReturn("jwt-token");

        ResponseDto response = authService.signup(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("john", response.getUsername());
        assertEquals("jwt-token", response.getJwtToken());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User captured = captor.getValue();

        assertEquals("john", captured.getUsername());
        assertEquals("encodedPassword", captured.getPassword());

        verify(authUtil).generateAccessToken(savedUser);
    }

    @Test
    void signup_ShouldThrowException_WhenUsernameAlreadyExists() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.signup(requestDto)
        );

        assertEquals("Username already exists", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(authUtil, never()).generateAccessToken(any());
    }

    @Test
    void login_ShouldReturnJwtToken() {

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new TestingAuthenticationToken("john", "password"));

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(authUtil.generateAccessToken(user))
                .thenReturn("jwt-token");

        ResponseDto response = authService.login(requestDto);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals(1L, response.getUserId());
        assertEquals("jwt-token", response.getJwtToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authUtil).generateAccessToken(user);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new TestingAuthenticationToken("john", "password"));

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(requestDto)
        );

        verify(authUtil, never()).generateAccessToken(any());
    }
}
