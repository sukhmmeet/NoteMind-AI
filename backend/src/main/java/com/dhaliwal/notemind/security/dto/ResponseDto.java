package com.dhaliwal.notemind.security.dto;

import lombok.Data;

@Data
public class ResponseDto {
    private String username;
    private Long userId;
    private String jwtToken;
}
