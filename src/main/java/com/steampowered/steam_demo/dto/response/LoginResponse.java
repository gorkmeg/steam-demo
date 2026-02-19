package com.steampowered.steam_demo.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String tokenType = "Bearer";
    private String token;
    private long expiresIn;
    private UserResponse user;
}
