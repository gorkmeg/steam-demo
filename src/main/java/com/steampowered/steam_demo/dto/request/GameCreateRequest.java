package com.steampowered.steam_demo.dto.request;

import com.steampowered.steam_demo.entity.GameType;
//import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

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
    @NotNull(message = "isDlc is required")
    private Boolean isDlc;
    private UUID baseGameId;
//    @AssertTrue(message = "baseGameId is required when isDlc is true")
//    public boolean isBaseGameIdValidForDlc() {
//        return !Boolean.TRUE.equals(isDlc) || baseGameId != null;
//    }
//    @AssertTrue(message = "baseGameId must be null when isDlc is false")
//    public boolean isBaseGameIdNullForBaseGame(){
//        return !Boolean.FALSE.equals(isDlc) || baseGameId == null;
//    }
}
