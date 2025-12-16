package com.quizapp.dto;

import com.quizapp.entity.Role;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private Role role;
    private String email;
    private Long userId;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = Role.valueOf(role);
    }

    public AuthResponse(String token, String username, Role role, String email, Long userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.email = email;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}