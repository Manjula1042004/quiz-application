package com.quizapp.security;

import com.quizapp.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtRequestFilter Tests")
class JwtRequestFilterTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // Clear security context
        SecurityContextHolder.clearContext();

        // Create user details
        userDetails = new User("testuser", "password", Collections.emptyList());
        validToken = "valid.jwt.token";
    }

    @Test
    @DisplayName("Should parse JWT from Authorization header")
    void parseJwt_ValidHeader_ReturnsToken() throws Exception {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        // When - Use reflection to test private method
        String token = (String) org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(jwtRequestFilter, "parseJwt", request);

        // Then
        assertEquals(validToken, token);
    }

    @Test
    @DisplayName("Should return null for missing Authorization header")
    void parseJwt_MissingHeader_ReturnsNull() throws Exception {
        // When
        String token = (String) org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(jwtRequestFilter, "parseJwt", request);

        // Then
        assertNull(token);
    }

    @Test
    @DisplayName("Should return null for empty Authorization header")
    void parseJwt_EmptyHeader_ReturnsNull() throws Exception {
        // Given
        request.addHeader("Authorization", "");

        // When
        String token = (String) org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(jwtRequestFilter, "parseJwt", request);

        // Then
        assertNull(token);
    }

    @Test
    @DisplayName("Should return null for malformed Authorization header")
    void parseJwt_MalformedHeader_ReturnsNull() throws Exception {
        // Given
        request.addHeader("Authorization", "InvalidPrefix " + validToken);

        // When
        String token = (String) org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(jwtRequestFilter, "parseJwt", request);

        // Then
        assertNull(token);
    }

    @Test
    @DisplayName("Should return null for Authorization header without Bearer")
    void parseJwt_HeaderWithoutBearer_ReturnsNull() throws Exception {
        // Given
        request.addHeader("Authorization", "Token " + validToken);

        // When
        String token = (String) org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(jwtRequestFilter, "parseJwt", request);

        // Then
        assertNull(token);
    }

    @Test
    @DisplayName("Should set authentication for valid JWT")
    void doFilterInternal_ValidJwt_SetsAuthentication() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(true);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication for invalid JWT")
    void doFilterInternal_InvalidJwt_DoesNotSetAuthentication() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer invalid.token");

        when(jwtUtil.validateToken("invalid.token")).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication for expired JWT")
    void doFilterInternal_ExpiredJwt_DoesNotSetAuthentication() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when user not found")
    void doFilterInternal_UserNotFound_DoesNotSetAuthentication() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("nonexistent");
        when(userDetailsService.loadUserByUsername("nonexistent")).thenReturn(null);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when authentication already exists")
    void doFilterInternal_AuthenticationExists_DoesNotOverride() throws ServletException, IOException {
        // Given - Authentication already set
        org.springframework.security.core.Authentication existingAuth =
                mock(org.springframework.security.core.Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        request.addHeader("Authorization", "Bearer " + validToken);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("Should continue filter chain on exception")
    void doFilterInternal_Exception_ContinuesFilterChain() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenThrow(new RuntimeException("JWT error"));

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle null JWT token")
    void doFilterInternal_NullJwt_ContinuesFilterChain() throws ServletException, IOException {
        // Given - No Authorization header

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("Should handle empty JWT token")
    void doFilterInternal_EmptyJwt_ContinuesFilterChain() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer ");

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("Should extract username from token")
    void doFilterInternal_ExtractsUsername() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(true);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil, times(1)).extractUsername(validToken);
        verify(userDetailsService, times(1)).loadUserByUsername("testuser");
    }

    @Test
    @DisplayName("Should validate token against user details")
    void doFilterInternal_ValidatesTokenWithUserDetails() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(true);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil, times(1)).validateToken(validToken, userDetails);
    }

    @Test
    @DisplayName("Should not authenticate when token validation fails with user details")
    void doFilterInternal_TokenValidationFails_NoAuthentication() throws ServletException, IOException {
        // Given
        request.addHeader("Authorization", "Bearer " + validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}