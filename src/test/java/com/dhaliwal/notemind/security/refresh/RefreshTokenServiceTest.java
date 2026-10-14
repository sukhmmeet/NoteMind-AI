package com.dhaliwal.notemind.security.refresh;

import com.dhaliwal.notemind.entity.RefreshToken;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.exception.InvalidRefreshTokenException;
import com.dhaliwal.notemind.exception.RefreshTokenExpiredException;
import com.dhaliwal.notemind.security.refresh.dto.RefreshRequestDto;
import com.dhaliwal.notemind.security.refresh.dto.RefreshResponseDto;
import com.dhaliwal.notemind.security.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshToken;
    private User user;
    private RefreshRequestDto request;

    @BeforeEach
    void setUp() {
        user = new User();

        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken("hashedToken");
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(
                Instant.now().plus(30, ChronoUnit.DAYS));

        request = RefreshRequestDto.builder()
                .refreshToken("plainToken")
                .build();
    }

    @Test
    void refreshToken_ShouldReturnNewTokens() {

        when(authUtil.hashToken("plainToken"))
                .thenReturn("hashedToken");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("hashedToken"))
                .thenReturn(Optional.of(refreshToken));

        when(authUtil.generateAccessToken(user))
                .thenReturn("jwt");

        when(authUtil.generateRefreshToken())
                .thenReturn("newRefresh");

        when(authUtil.hashToken("newRefresh"))
                .thenReturn("hashedNewRefresh");

        RefreshResponseDto response =
                refreshTokenService.refreshToken(request);

        assertEquals("jwt", response.getJwtToken());
        assertEquals("newRefresh", response.getRefreshToken());

        verify(refreshTokenRepository, times(1))
                .save(refreshToken);

        assertEquals("hashedNewRefresh",
                refreshToken.getToken());

        assertFalse(refreshToken.isRevoked());
    }

    @Test
    void refreshToken_ShouldThrow_WhenTokenNotFound() {

        when(authUtil.hashToken("plainToken"))
                .thenReturn("hashedToken");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("hashedToken"))
                .thenReturn(Optional.empty());

        InvalidRefreshTokenException exception =
                assertThrows(
                        InvalidRefreshTokenException.class,
                        () -> refreshTokenService.refreshToken(request)
                );

        assertEquals("Invalid refresh token",
                exception.getMessage());

        verify(refreshTokenRepository, never())
                .save(any());
    }

    @Test
    void refreshToken_ShouldRevokeAndThrow_WhenExpired() {

        refreshToken.setExpiryDate(
                Instant.now().minusSeconds(10));

        when(authUtil.hashToken("plainToken"))
                .thenReturn("hashedToken");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("hashedToken"))
                .thenReturn(Optional.of(refreshToken));

        RefreshTokenExpiredException exception =
                assertThrows(
                        RefreshTokenExpiredException.class,
                        () -> refreshTokenService.refreshToken(request)
                );

        assertEquals("Refresh token expired",
                exception.getMessage());

        assertTrue(refreshToken.isRevoked());

        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void logout_ShouldRevokeToken() {

        when(authUtil.hashToken("plainToken"))
                .thenReturn("hashedToken");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("hashedToken"))
                .thenReturn(Optional.of(refreshToken));

        refreshTokenService.logout(request);

        assertTrue(refreshToken.isRevoked());

        assertTrue(
                refreshToken.getExpiryDate()
                        .isBefore(Instant.now().plusSeconds(1))
        );

        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void logout_ShouldThrow_WhenTokenNotFound() {

        when(authUtil.hashToken("plainToken"))
                .thenReturn("hashedToken");

        when(refreshTokenRepository.findByTokenAndRevokedFalse("hashedToken"))
                .thenReturn(Optional.empty());

        InvalidRefreshTokenException exception =
                assertThrows(
                        InvalidRefreshTokenException.class,
                        () -> refreshTokenService.logout(request)
                );

        assertEquals("Invalid refresh token",
                exception.getMessage());

        verify(refreshTokenRepository, never())
                .save(any());
    }
}