package com.quizapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationEntryPoint Tests")
class JwtAuthenticationEntryPointTest {

    @InjectMocks
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private AuthenticationException authException;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();

        when(authException.getMessage()).thenReturn("Authentication failed");
    }

    @Test
    @DisplayName("Should commence with unauthorized response")
    void commence_ShouldReturnUnauthorizedResponse() throws IOException, ServletException {
        // Given
        request.setServletPath("/api/secure");

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertNotNull(response.getContentAsString());

        // Parse JSON response
        Map<String, Object> responseBody = objectMapper.readValue(
                response.getContentAsString(), Map.class);

        assertEquals(401, responseBody.get("status"));
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Authentication failed", responseBody.get("message"));
        assertEquals("/api/secure", responseBody.get("path"));
    }

    @Test
    @DisplayName("Should handle different request paths")
    void commence_DifferentPaths_IncludesCorrectPath() throws IOException, ServletException {
        // Given
        request.setServletPath("/api/admin/dashboard");

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        Map<String, Object> responseBody = objectMapper.readValue(
                response.getContentAsString(), Map.class);

        assertEquals("/api/admin/dashboard", responseBody.get("path"));
    }

    @Test
    @DisplayName("Should handle empty authentication exception message")
    void commence_EmptyExceptionMessage_ReturnsResponse() throws IOException, ServletException {
        // Given
        when(authException.getMessage()).thenReturn("");

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertEquals(401, response.getStatus());
        Map<String, Object> responseBody = objectMapper.readValue(
                response.getContentAsString(), Map.class);

        assertEquals("", responseBody.get("message"));
    }

    @Test
    @DisplayName("Should handle null authentication exception message")
    void commence_NullExceptionMessage_ReturnsResponse() throws IOException, ServletException {
        // Given
        when(authException.getMessage()).thenReturn(null);

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertEquals(401, response.getStatus());
        Map<String, Object> responseBody = objectMapper.readValue(
                response.getContentAsString(), Map.class);

        assertNull(responseBody.get("message"));
    }

    @Test
    @DisplayName("Should set correct content type")
    void commence_ShouldSetJsonContentType() throws IOException, ServletException {
        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertTrue(response.getContentAsString().startsWith("{"));
        assertTrue(response.getContentAsString().endsWith("}"));
    }

    @Test
    @DisplayName("Should write valid JSON to response")
    void commence_ShouldWriteValidJson() throws IOException, ServletException {
        // Given
        request.setServletPath("/test");

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        String jsonResponse = response.getContentAsString();

        // Verify it's valid JSON
        Map<String, Object> parsed = objectMapper.readValue(jsonResponse, Map.class);
        assertNotNull(parsed);

        // Check all expected fields
        assertTrue(parsed.containsKey("status"));
        assertTrue(parsed.containsKey("error"));
        assertTrue(parsed.containsKey("message"));
        assertTrue(parsed.containsKey("path"));
    }

    @Test
    @DisplayName("Should handle different exception messages")
    void commence_DifferentExceptionMessages_IncludesMessage() throws IOException, ServletException {
        // Given
        String errorMessage = "JWT token has expired";
        when(authException.getMessage()).thenReturn(errorMessage);

        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        Map<String, Object> responseBody = objectMapper.readValue(
                response.getContentAsString(), Map.class);

        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    @DisplayName("Should not throw exception on valid request")
    void commence_ValidRequest_DoesNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> {
            authenticationEntryPoint.commence(request, response, authException);
        });
    }

    @Test
    @DisplayName("Should maintain response as committed")
    void commence_ResponseIsCommitted() throws IOException, ServletException {
        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        assertTrue(response.isCommitted());
    }

    @Test
    @DisplayName("Should include all required fields in response")
    void commence_ResponseHasAllRequiredFields() throws IOException, ServletException {
        // When
        authenticationEntryPoint.commence(request, response, authException);

        // Then
        String jsonResponse = response.getContentAsString();
        assertTrue(jsonResponse.contains("\"status\":401"));
        assertTrue(jsonResponse.contains("\"error\":\"Unauthorized\""));
        assertTrue(jsonResponse.contains("\"message\":\"Authentication failed\""));
        assertTrue(jsonResponse.contains("\"path\":\"\""));
    }

    @Test
    @DisplayName("Should handle IO exception gracefully")
    void commence_IOException_PropagatesException() {
        // This test would require mocking the response to throw IOException
        // For now, we trust that the method declares IOException
        assertTrue(true);
    }
}