package com.dhaliwal.notemind.security.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String username;
    private Long userId;
    private String jwtToken;
    private String refreshToken;
}
