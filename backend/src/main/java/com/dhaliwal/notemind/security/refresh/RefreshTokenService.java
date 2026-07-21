package com.dhaliwal.notemind.security.refresh;

import com.dhaliwal.notemind.entity.RefreshToken;
import com.dhaliwal.notemind.exception.InvalidRefreshTokenException;
import com.dhaliwal.notemind.exception.RefreshTokenExpiredException;
import com.dhaliwal.notemind.security.refresh.dto.RefreshRequestDto;
import com.dhaliwal.notemind.security.refresh.dto.RefreshResponseDto;
import com.dhaliwal.notemind.security.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;

    @Transactional
    public RefreshResponseDto refreshToken(RefreshRequestDto refreshRequestDto) {
        String hash = authUtil.hashToken(refreshRequestDto.getRefreshToken());

        RefreshToken refreshToken =
                refreshTokenRepository.findByTokenAndRevokedFalse(hash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException("Invalid refresh token"));

        if (Instant.now().isAfter(refreshToken.getExpiryDate())) {

            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        String jwt = authUtil.generateAccessToken(refreshToken.getUser());

        String newRefreshToken = authUtil.generateRefreshToken();

        refreshToken.setToken(authUtil.hashToken(newRefreshToken));
        refreshToken.setExpiryDate(
                Instant.now().plus(30, ChronoUnit.DAYS)
        );

        refreshTokenRepository.save(refreshToken);

        return RefreshResponseDto.builder()
                .jwtToken(jwt)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(RefreshRequestDto refreshRequestDto) {

        String hash = authUtil.hashToken(refreshRequestDto.getRefreshToken());

        RefreshToken refreshToken =
                refreshTokenRepository.findByTokenAndRevokedFalse(hash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException("Invalid refresh token"));

        refreshToken.setExpiryDate(Instant.now());
        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

}
