package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.security.dto.AuthRequestDto;
import com.dhaliwal.notemind.security.dto.AuthResponseDto;
import com.dhaliwal.notemind.security.refresh.RefreshTokenService;
import com.dhaliwal.notemind.security.refresh.dto.RefreshRequestDto;
import com.dhaliwal.notemind.security.refresh.dto.RefreshResponseDto;
import com.dhaliwal.notemind.security.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLogin() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("testuser");
        request.setPassword("password");

        AuthResponseDto response = new AuthResponseDto();
        response.setJwtToken("dummy-jwt-token");
        response.setRefreshToken("dummy-refresh-token");

        when(authService.login(any(AuthRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.jwtToken").value("dummy-jwt-token"));
    }

    @Test
    void testSignup() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("newuser");
        request.setPassword("password");

        AuthResponseDto response = new AuthResponseDto();
        response.setJwtToken("dummy-jwt-token");

        when(authService.signup(any(AuthRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Signup successful"))
                .andExpect(jsonPath("$.data.jwtToken").value("dummy-jwt-token"));
    }

    @Test
    void testRefreshToken() throws Exception {
        RefreshRequestDto request = RefreshRequestDto.builder()
                .refreshToken("old-refresh-token")
                .build();

        RefreshResponseDto response = RefreshResponseDto.builder()
                .jwtToken("new-jwt-token")
                .refreshToken("new-refresh-token")
                .build();

        when(refreshTokenService.refreshToken(any(RefreshRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.jwtToken").value("new-jwt-token"));
    }

    @Test
    void testLogout() throws Exception {
        RefreshRequestDto request = RefreshRequestDto.builder()
                .refreshToken("refresh-token-to-invalidate")
                .build();

        doNothing().when(refreshTokenService).logout(any(RefreshRequestDto.class));

        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}
