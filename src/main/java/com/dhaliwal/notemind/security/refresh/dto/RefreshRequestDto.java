package com.dhaliwal.notemind.security.refresh.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshRequestDto {
    private String refreshToken;
}
