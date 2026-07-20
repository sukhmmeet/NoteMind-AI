package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.security.refresh.RefreshTokenService;
import com.dhaliwal.notemind.security.refresh.dto.RefreshRequestDto;
import com.dhaliwal.notemind.security.refresh.dto.RefreshResponseDto;
import com.dhaliwal.notemind.security.service.AuthService;
import com.dhaliwal.notemind.security.dto.AuthRequestDto;
import com.dhaliwal.notemind.security.dto.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto loginAuthRequestDto) {
        return ResponseEntity.ok(authService.login(loginAuthRequestDto));
    }
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthRequestDto signupAuthRequestDto) {
        try{
            return ResponseEntity.ok(authService.signup(signupAuthRequestDto));
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshResponseDto> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(refreshRequestDto));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshRequestDto refreshRequestDto) {
        refreshTokenService.logout(refreshRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
