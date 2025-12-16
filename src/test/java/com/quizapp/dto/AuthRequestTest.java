package com.quizapp.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testNoArgsConstructor() {
        AuthRequest authRequest = new AuthRequest();

        assertNull(authRequest.getUsername());
        assertNull(authRequest.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        AuthRequest authRequest = new AuthRequest("john", "password123");

        assertEquals("john", authRequest.getUsername());
        assertEquals("password123", authRequest.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("jane");
        authRequest.setPassword("securePass123");

        assertEquals("jane", authRequest.getUsername());
        assertEquals("securePass123", authRequest.getPassword());
    }

    @Test
    void testValidation_ValidData() {
        AuthRequest authRequest = new AuthRequest("validUser", "validPassword");

        var violations = validator.validate(authRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankUsername() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("");
        authRequest.setPassword("password123");

        var violations = validator.validate(authRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_BlankPassword() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("john");
        authRequest.setPassword("");

        var violations = validator.validate(authRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_BothFieldsBlank() {
        AuthRequest authRequest = new AuthRequest("", "");

        var violations = validator.validate(authRequest);
        assertEquals(2, violations.size());
    }

    @Test
    void testToString() {
        AuthRequest authRequest = new AuthRequest("testUser", "testPass");
        String toString = authRequest.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("testUser"));
    }

    @Test
    void testEqualsAndHashCode() {
        AuthRequest request1 = new AuthRequest("user1", "pass1");
        AuthRequest request2 = new AuthRequest("user1", "pass1");
        AuthRequest request3 = new AuthRequest("user2", "pass2");

        assertEquals(request1, request1); // Same object
        assertNotEquals(request1, null); // Not null
        assertNotEquals(request1, new Object()); // Different class

        // Based on field values
        assertEquals(request1.getUsername(), request2.getUsername());
        assertEquals(request1.getPassword(), request2.getPassword());
        assertNotEquals(request1, request3);
    }
}