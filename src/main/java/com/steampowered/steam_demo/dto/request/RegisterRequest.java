package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "displayName is required")
    private String displayName;
    @NotBlank(message = "password is required")
    private String password;
}
