package com.quizapp.dto;

import com.quizapp.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthResponseTest {

    @Test
    void testConstructorWithStringRole() {
        // Change USER to PARTICIPANT
        AuthResponse response = new AuthResponse("jwt-token", "john", "PARTICIPANT");

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("john", response.getUsername());
        assertEquals(Role.PARTICIPANT, response.getRole()); // Changed from USER to PARTICIPANT
        assertNull(response.getEmail());
        assertNull(response.getUserId());
    }

    @Test
    void testConstructorWithEnumRole() {
        // Change Role.USER to Role.PARTICIPANT
        AuthResponse response = new AuthResponse(
                "jwt-token",
                "jane",
                Role.PARTICIPANT, // Changed from USER to PARTICIPANT
                "jane@example.com",
                123L
        );

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("jane", response.getUsername());
        assertEquals(Role.PARTICIPANT, response.getRole()); // Changed from USER to PARTICIPANT
        assertEquals("jane@example.com", response.getEmail());
        assertEquals(123L, response.getUserId());
    }

    @Test
    void testStringRoleConversion() {
        // Change "USER" to "PARTICIPANT"
        AuthResponse userResponse = new AuthResponse("token1", "user1", "PARTICIPANT");
        assertEquals(Role.PARTICIPANT, userResponse.getRole());

        AuthResponse adminResponse = new AuthResponse("token2", "admin1", "ADMIN");
        assertEquals(Role.ADMIN, adminResponse.getRole());
    }

    // Update all other places where Role.USER is used
}