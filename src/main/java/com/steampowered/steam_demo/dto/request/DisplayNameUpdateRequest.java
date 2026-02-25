package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisplayNameUpdateRequest {
    @NotBlank(message = "Display Name cannot be blank.")
    private String displayName;
}
