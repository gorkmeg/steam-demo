package com.steampowered.steam_demo.dto.request;

import com.steampowered.steam_demo.entity.GameType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GameCreateRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "description is required")
    private String description;
    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be positive")
    private BigDecimal price;
    @NotNull(message = "gameType is required")
    private GameType gameType;
}
