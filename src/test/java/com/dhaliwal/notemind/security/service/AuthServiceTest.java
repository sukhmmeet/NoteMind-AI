package com.dhaliwal.notemind.security.service;

import com.dhaliwal.notemind.entity.RefreshToken;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.repository.UserRepository;
import com.dhaliwal.notemind.security.dto.AuthRequestDto;
import com.dhaliwal.notemind.security.dto.AuthResponseDto;
import com.dhaliwal.notemind.security.refresh.RefreshTokenRepository;
import com.dhaliwal.notemind.security.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    private AuthRequestDto request;
    private User user;

    @BeforeEach
    void setUp() {

        request = new AuthRequestDto();
        request.setUsername("dhaliwal");
        request.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setUsername("dhaliwal");
        user.setPassword("encodedPassword");
    }

    @Test
    void signup_ShouldCreateUserSuccessfully() {

        when(userRepository.findByUsername("dhaliwal"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(authUtil.generateRefreshToken())
                .thenReturn("refresh");

        when(authUtil.hashToken("refresh"))
                .thenReturn("hashedRefresh");

        when(authUtil.generateAccessToken(user))
                .thenReturn("jwt");

        AuthResponseDto response = authService.signup(request);

        assertEquals("dhaliwal", response.getUsername());
        assertEquals(1L, response.getUserId());
        assertEquals("jwt", response.getJwtToken());
        assertEquals("refresh", response.getRefreshToken());

        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void signup_ShouldThrow_WhenUsernameAlreadyExists() {

        when(userRepository.findByUsername("dhaliwal"))
                .thenReturn(Optional.of(user));

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class,
                        () -> authService.signup(request));

        assertEquals("Username already exists",
                exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnJwtAndRefreshToken() {

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUsername("dhaliwal"))
                .thenReturn(Optional.of(user));

        when(authUtil.generateAccessToken(user))
                .thenReturn("jwt");

        when(authUtil.generateRefreshToken())
                .thenReturn("refresh");

        when(authUtil.hashToken("refresh"))
                .thenReturn("hashedRefresh");

        AuthResponseDto response = authService.login(request);

        assertEquals("dhaliwal", response.getUsername());
        assertEquals(1L, response.getUserId());
        assertEquals("jwt", response.getJwtToken());
        assertEquals("refresh", response.getRefreshToken());

        verify(authenticationManager).authenticate(any(
                UsernamePasswordAuthenticationToken.class));

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByUsername("dhaliwal"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> authService.login(request)
                );

        assertEquals("User not found",
                exception.getMessage());

        verify(refreshTokenRepository, never()).save(any());
    }
}