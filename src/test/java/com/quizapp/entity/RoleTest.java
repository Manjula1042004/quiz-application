package com.quizapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Enum Tests")
class RoleTest {

    @Test
    @DisplayName("Should have all roles")
    void shouldHaveAllRoles() {
        // When
        Role[] roles = Role.values();

        // Then
        assertEquals(2, roles.length);
        assertArrayEquals(new Role[]{
                Role.ADMIN,
                Role.PARTICIPANT
        }, roles);
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    @DisplayName("Should have valid enum values")
    void shouldHaveValidEnumValues(Role role) {
        // Then
        assertNotNull(role);
        assertNotNull(role.name());
        assertFalse(role.name().isEmpty());
    }

    @Test
    @DisplayName("Should convert string to enum")
    void valueOf_ValidString_ReturnsEnum() {
        // When & Then
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.PARTICIPANT, Role.valueOf("PARTICIPANT"));
    }

    @Test
    @DisplayName("Should have correct string representations")
    void toString_ReturnsCorrectString() {
        // When & Then
        assertEquals("ADMIN", Role.ADMIN.toString());
        assertEquals("PARTICIPANT", Role.PARTICIPANT.toString());
    }

    @Test
    @DisplayName("Should compare enum values")
    void compareEnumValues() {
        // When & Then
        assertTrue(Role.ADMIN != Role.PARTICIPANT);
        assertEquals(Role.ADMIN, Role.ADMIN);
        assertEquals(Role.PARTICIPANT, Role.PARTICIPANT);
    }

    @Test
    @DisplayName("Should get enum by ordinal")
    void values_ReturnsByOrdinal() {
        // When & Then
        assertEquals(Role.ADMIN, Role.values()[0]);
        assertEquals(Role.PARTICIPANT, Role.values()[1]);
    }

    @Test
    @DisplayName("Should handle enum ordinal values")
    void ordinal_ReturnsCorrectIndex() {
        // When & Then
        assertEquals(0, Role.ADMIN.ordinal());
        assertEquals(1, Role.PARTICIPANT.ordinal());
    }

    @Test
    @DisplayName("Should iterate through all enum values")
    void values_Iteration() {
        // When
        int count = 0;
        for (Role role : Role.values()) {
            assertNotNull(role);
            count++;
        }

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should use enum in switch statement")
    void enumInSwitchStatement() {
        // Given
        Role role = Role.ADMIN;
        String result = "";

        // When
        switch (role) {
            case ADMIN:
                result = "Administrator";
                break;
            case PARTICIPANT:
                result = "Participant";
                break;
        }

        // Then
        assertEquals("Administrator", result);
    }

    @Test
    @DisplayName("Should check if enum contains value")
    void valueOf_ContainsValue() {
        // When & Then
        assertTrue(containsRole("ADMIN"));
        assertTrue(containsRole("PARTICIPANT"));
        assertFalse(containsRole("MODERATOR"));
        assertFalse(containsRole("GUEST"));
    }

    private boolean containsRole(String name) {
        try {
            Role.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    @DisplayName("Should check role permissions")
    void checkRolePermissions() {
        // When
        boolean isAdmin = Role.ADMIN == Role.ADMIN;
        boolean isParticipant = Role.PARTICIPANT == Role.PARTICIPANT;
        boolean adminIsNotParticipant = Role.ADMIN != Role.PARTICIPANT;

        // Then
        assertTrue(isAdmin);
        assertTrue(isParticipant);
        assertTrue(adminIsNotParticipant);
    }

    @Test
    @DisplayName("Should handle case sensitivity")
    void valueOf_CaseSensitive() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("admin"); // lowercase should fail
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("Participant"); // mixed case should fail
        });
    }

    @Test
    @DisplayName("Should get all roles as strings")
    void getAllRoleNames() {
        // When
        String[] roleNames = new String[Role.values().length];
        for (int i = 0; i < Role.values().length; i++) {
            roleNames[i] = Role.values()[i].name();
        }

        // Then
        assertEquals(2, roleNames.length);
        assertEquals("ADMIN", roleNames[0]);
        assertEquals("PARTICIPANT", roleNames[1]);
    }

    @Test
    @DisplayName("Should use enum in if-else statements")
    void enumInIfElseStatements() {
        // Given
        Role role = Role.PARTICIPANT;
        String accessLevel = "";

        // When
        if (role == Role.ADMIN) {
            accessLevel = "Full Access";
        } else if (role == Role.PARTICIPANT) {
            accessLevel = "Limited Access";
        }

        // Then
        assertEquals("Limited Access", accessLevel);
    }
}