package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DisplayNameUpdateRequest {
    @NotBlank(message = "Display name cannot be empty")
    private String displayName;
}
