package com.dhaliwal.notemind.security.refresh;

import com.dhaliwal.notemind.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
}
