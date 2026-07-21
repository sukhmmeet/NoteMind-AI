package com.dhaliwal.notemind.controller;

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
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody AuthRequestDto authRequestDto) {

        return ResponseEntity.ok(authService.login(authRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(
            @RequestBody AuthRequestDto authRequestDto) {

        return ResponseEntity.ok(authService.signup(authRequestDto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshResponseDto> refreshToken(
            @RequestBody RefreshRequestDto refreshRequestDto) {

        return ResponseEntity.ok(
                refreshTokenService.refreshToken(refreshRequestDto)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshRequestDto refreshRequestDto) {

        refreshTokenService.logout(refreshRequestDto);
        return ResponseEntity.ok().build();
    }
}