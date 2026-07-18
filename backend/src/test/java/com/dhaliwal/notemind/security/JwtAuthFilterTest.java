package com.dhaliwal.notemind.security;

import com.dhaliwal.notemind.entity.User;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsMissing() throws Exception {

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(authUtil, userRepository);
    }

    @Test
    void shouldContinueWhenHeaderIsNotBearerToken() throws Exception {

        request.addHeader("Authorization", "Basic abc123");

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(authUtil, userRepository);
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception {

        User user = new User();
        user.setUsername("john");

        request.addHeader("Authorization", "Bearer token123");

        when(authUtil.getUsernameFromToken("token123"))
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        jwtAuthFilter.doFilter(request, response, filterChain);

        var authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());

        verify(authUtil).getUsernameFromToken("token123");
        verify(userRepository).findByUsername("john");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        request.addHeader("Authorization", "Bearer token123");

        when(authUtil.getUsernameFromToken("token123"))
                .thenReturn("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> jwtAuthFilter.doFilter(request, response, filterChain)
        );

        verify(userRepository).findByUsername("john");
    }

    @Test
    void shouldNotAuthenticateWhenAuthenticationAlreadyExists() throws Exception {

        User existingUser = new User();

        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken(
                        existingUser,
                        null,
                        existingUser.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuth);

        request.addHeader("Authorization", "Bearer token123");

        when(authUtil.getUsernameFromToken("token123"))
                .thenReturn("john");

        jwtAuthFilter.doFilter(request, response, filterChain);

        verify(authUtil).getUsernameFromToken("token123");

        verify(userRepository, never()).findByUsername(any());

        assertEquals(
                existingAuth,
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }
}