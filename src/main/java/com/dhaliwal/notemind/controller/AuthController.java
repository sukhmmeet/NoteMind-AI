package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.ApiResponse;
import com.dhaliwal.notemind.security.dto.AuthRequestDto;
import com.dhaliwal.notemind.security.dto.AuthResponseDto;
import com.dhaliwal.notemind.security.refresh.RefreshTokenService;
import com.dhaliwal.notemind.security.refresh.dto.RefreshRequestDto;
import com.dhaliwal.notemind.security.refresh.dto.RefreshResponseDto;
import com.dhaliwal.notemind.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(authRequestDto)));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponseDto>> signup(
            @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("Signup successful", authService.signup(authRequestDto)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshResponseDto>> refreshToken(
            @RequestBody RefreshRequestDto refreshRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(refreshTokenService.refreshToken(refreshRequestDto)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody RefreshRequestDto refreshRequestDto) {
        refreshTokenService.logout(refreshRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}