package com.dhaliwal.notemind.security.refresh.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResponseDto {
    private String refreshToken;
    private String jwtToken;
}
