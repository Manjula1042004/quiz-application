package com.quizapp.security;

import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    private User testUser;
    private String validToken;
    private final String secret = "testSecretKeyForJWTTokenGenerationInOnlineQuizApplicationTest";
    private final long expiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.PARTICIPANT);

        // Generate a valid token for testing
        validToken = jwtUtil.generateToken(testUser);
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_ValidToken_ReturnsUsername() {
        // When
        String username = jwtUtil.extractUsername(validToken);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void extractExpiration_ValidToken_ReturnsDate() {
        // When
        Date expirationDate = jwtUtil.extractExpiration(validToken);

        // Then
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    @DisplayName("Should extract specific claim from token")
    void extractClaim_ValidToken_ReturnsClaim() {
        // When
        String username = jwtUtil.extractClaim(validToken, Claims::getSubject);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should generate token for UserDetails")
    void generateToken_UserDetails_ReturnsToken() {
        // Given
        when(userDetails.getUsername()).thenReturn("userdetailsuser");

        // When
        String token = jwtUtil.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("userdetailsuser", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Should generate token for User entity")
    void generateToken_UserEntity_ReturnsToken() {
        // When
        String token = jwtUtil.generateToken(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Extract username from token
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should validate token with UserDetails")
    void validateToken_ValidTokenAndUserDetails_ReturnsTrue() {
        // Given
        UserDetails testUserDetails = new org.springframework.security.core.userdetails.User(
                "testuser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTICIPANT")));

        // Generate token for this user
        String token = jwtUtil.generateToken(testUserDetails);

        // When
        boolean isValid = jwtUtil.validateToken(token, testUserDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for invalid token with UserDetails")
    void validateToken_InvalidTokenAndUserDetails_ReturnsFalse() {
        // Given
        UserDetails testUserDetails1 = new org.springframework.security.core.userdetails.User(
                "user1", "password", Collections.emptyList());

        UserDetails testUserDetails2 = new org.springframework.security.core.userdetails.User(
                "user2", "password", Collections.emptyList());

        // Generate token for user1
        String token = jwtUtil.generateToken(testUserDetails1);

        // When - Try to validate with user2
        boolean isValid = jwtUtil.validateToken(token, testUserDetails2);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should validate token without UserDetails")
    void validateToken_ValidToken_ReturnsTrue() {
        // When
        boolean isValid = jwtUtil.validateToken(validToken);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for expired token")
    void validateToken_ExpiredToken_ReturnsFalse() throws InterruptedException {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms expiration
        String shortLivedToken = jwtUtil.generateToken(testUser);

        // Wait for token to expire
        Thread.sleep(10);

        // Reset expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);

        // When
        boolean isValid = jwtUtil.validateToken(shortLivedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for malformed token")
    void validateToken_MalformedToken_ReturnsFalse() {
        // Given
        String malformedToken = "malformed.token.here";

        // When
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for tampered token")
    void validateToken_TamperedToken_ReturnsFalse() {
        // Given - Take a valid token and modify it
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "xxxxx";

        // When
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should get role from token")
    void getRoleFromToken_ValidToken_ReturnsRole() {
        // When
        String role = jwtUtil.getRoleFromToken(validToken);

        // Then
        assertEquals(Role.PARTICIPANT.name(), role);
    }

    @Test
    @DisplayName("Should get user ID from token")
    void getUserIdFromToken_ValidToken_ReturnsUserId() {
        // When
        Long userId = jwtUtil.getUserIdFromToken(validToken);

        // Then
        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("Should throw exception for expired token when extracting username")
    void extractUsername_ExpiredToken_ThrowsException() throws InterruptedException {
        // Given - Create a token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1ms expiration
        String shortLivedToken = jwtUtil.generateToken(testUser);

        // Wait for token to expire
        Thread.sleep(10);

        // Reset expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);

        // When & Then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.extractUsername(shortLivedToken);
        });
    }

    @Test
    @DisplayName("Should handle different secret keys")
    void validateToken_DifferentSecret_ReturnsFalse() {
        // Given - Create another JwtUtil with different secret
        JwtUtil differentJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(differentJwtUtil, "secret", "differentSecretKeyForTesting1234567890");
        ReflectionTestUtils.setField(differentJwtUtil, "expiration", expiration);

        // Generate token with different secret
        String differentToken = differentJwtUtil.generateToken(testUser);

        // When - Try to validate with original jwtUtil
        boolean isValid = jwtUtil.validateToken(differentToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle null or empty token")
    void validateToken_NullOrEmptyToken_ReturnsFalse() {
        // When & Then
        assertFalse(jwtUtil.validateToken(null));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken("   "));
    }

    @Test
    @DisplayName("Should extract username from token with User entity")
    void extractUsername_TokenWithUserEntity_ReturnsUsername() {
        // When
        String username = jwtUtil.extractUsername(validToken);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should create token with claims using reflection")
    void createToken_WithClaims_ReturnsToken() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("customClaim", "customValue");
        claims.put("numberClaim", 123);

        // When - Using reflection to test private method
        String token = (String) ReflectionTestUtils.invokeMethod(
                jwtUtil, "createToken", claims, "testsubject");

        // Then
        assertNotNull(token);
        // Can't test claims directly since extractAllClaims is private
        // But we can test the token is valid
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("testsubject", jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Should handle token with different user roles")
    void generateToken_DifferentRoles_IncludesRoleInToken() {
        // Given
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        // When
        String adminToken = jwtUtil.generateToken(adminUser);

        // Then
        String role = jwtUtil.getRoleFromToken(adminToken);
        assertEquals(Role.ADMIN.name(), role);
    }

    @Test
    @DisplayName("Should handle token generation for null user")
    void generateToken_NullUser_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            jwtUtil.generateToken((User) null);
        });
    }

    @Test
    @DisplayName("Should handle token generation for null UserDetails")
    void generateToken_NullUserDetails_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            jwtUtil.generateToken((UserDetails) null);
        });
    }

    @Test
    @DisplayName("Should handle extraction from null token")
    void extractUsername_NullToken_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername(null);
        });
    }

    @Test
    @DisplayName("Should handle extraction from empty token")
    void extractUsername_EmptyToken_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername("");
        });
    }
}