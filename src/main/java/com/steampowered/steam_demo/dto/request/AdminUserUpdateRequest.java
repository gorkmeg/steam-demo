package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdminUserUpdateRequest {
    @NotNull(message = "id is required")
    private UUID id;

    @NotBlank(message = "Display name cannot be empty")
    private String name;
}
