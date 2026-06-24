package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.security.AuthService;
import com.dhaliwal.notemind.security.dto.RequestDto;
import com.dhaliwal.notemind.security.dto.ResponseDto;
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
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody RequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody RequestDto signupRequestDto) {
        try{
            return ResponseEntity.ok(authService.signup(signupRequestDto));
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }
}
