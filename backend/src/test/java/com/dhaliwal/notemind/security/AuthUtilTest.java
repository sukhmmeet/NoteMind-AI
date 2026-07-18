package com.dhaliwal.notemind.security;

import com.dhaliwal.notemind.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class AuthUtilTest {

    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        authUtil = new AuthUtil();

        ReflectionTestUtils.setField(
                authUtil,
                "secretKey",
                "thisIsASecretKeyThatIsLongEnoughForHS256Algorithm123456"
        );
    }

    @Test
    void shouldGenerateAccessToken() {

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        String token = authUtil.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsernameFromToken() {

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        String token = authUtil.generateAccessToken(user);

        String username = authUtil.getUsernameFromToken(token);

        assertEquals("john", username);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {

        assertThrows(
                MalformedJwtException.class,
                () -> authUtil.getUsernameFromToken("invalid-token")
        );
    }
    @Test
    void shouldReturnTrueForValidToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        String token = authUtil.generateAccessToken(user);

        assertTrue(authUtil.isTokenValid(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        assertFalse(authUtil.isTokenValid("abc"));
    }
}