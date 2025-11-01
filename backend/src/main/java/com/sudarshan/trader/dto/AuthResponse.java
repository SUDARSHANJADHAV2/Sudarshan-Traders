package com.sudarshan.trader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private UserDTO user;
    private String message;

    // Constructor for registration success (no token)
    public AuthResponse(String message, Long userId) {
        this.message = message;
        this.user = new UserDTO();
        this.user.setId(userId);
    }

    // Constructor for login success (with token)
    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}
