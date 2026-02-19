package com.steampowered.steam_demo.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
