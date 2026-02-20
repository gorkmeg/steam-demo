package com.steampowered.steam_demo.dto.request;

import com.steampowered.steam_demo.entity.GameType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class GameCreateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private GameType gameType;
}
