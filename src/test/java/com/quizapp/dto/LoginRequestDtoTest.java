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
class LoginRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultConstructor() {
        LoginRequestDto dto = new LoginRequestDto();

        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        LoginRequestDto dto = new LoginRequestDto("john", "password123");

        assertEquals("john", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("jane");
        dto.setPassword("securePass123");

        assertEquals("jane", dto.getUsername());
        assertEquals("securePass123", dto.getPassword());
    }

    @Test
    void testValidation_ValidData() {
        LoginRequestDto dto = new LoginRequestDto("validUser", "validPassword");

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankUsername() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("");
        dto.setPassword("password123");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_BlankPassword() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("john");
        dto.setPassword("");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_BothFieldsBlank() {
        LoginRequestDto dto = new LoginRequestDto("", "");

        var violations = validator.validate(dto);
        assertEquals(2, violations.size());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginRequestDto dto1 = new LoginRequestDto("user1", "pass1");
        LoginRequestDto dto2 = new LoginRequestDto("user1", "pass1");
        LoginRequestDto dto3 = new LoginRequestDto("user2", "pass2");

        assertEquals(dto1, dto1);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        LoginRequestDto dto = new LoginRequestDto("testUser", "testPass");
        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("testUser"));
        // Password should not be in toString for security
        assertFalse(toString.contains("testPass"));
    }
}