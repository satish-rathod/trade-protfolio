package com.apm.dto;

/**
 * Response DTO for authentication operations.
 */
public class AuthResponse {

    private String token;
    private String email;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String message) {
        this.token = token;
        this.email = email;
        this.message = message;
    }

    public static AuthResponse success(String token, String email) {
        return new AuthResponse(token, email, "Authentication successful");
    }

    public static AuthResponse registered(String email) {
        return new AuthResponse(null, email, "Account created successfully");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
