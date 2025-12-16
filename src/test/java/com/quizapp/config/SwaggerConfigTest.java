package com.quizapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SwaggerConfig Tests")
class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    @DisplayName("Should create OpenAPI bean with correct configuration")
    void customOpenAPI_ShouldReturnConfiguredOpenAPI() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());

        Info info = openAPI.getInfo();
        assertEquals("Online Quiz Application API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertTrue(info.getDescription().contains("Complete API documentation"));
    }

    @Test
    @DisplayName("Should configure security scheme")
    void customOpenAPI_ShouldHaveSecurityScheme() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Components components = openAPI.getComponents();

        // Then
        assertNotNull(components);
        assertNotNull(components.getSecuritySchemes());
        assertTrue(components.getSecuritySchemes().containsKey("bearerAuth"));

        SecurityScheme securityScheme = components.getSecuritySchemes().get("bearerAuth");
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }
}