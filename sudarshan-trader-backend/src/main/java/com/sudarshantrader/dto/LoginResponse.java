package com.sudarshantrader.backend.dto;

public class LoginResponse {
    private String token;
    private String role;
    private boolean verified;

    public LoginResponse(String token, String role, boolean verified) {
        this.token = token;
        this.role = role;
        this.verified = verified;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return verified;
    }
}
