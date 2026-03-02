package com.steampowered.steam_demo.dto.response;

import com.steampowered.steam_demo.entity.GameType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminGameRowResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final GameType gameType;
    private final LocalDateTime releaseDate;
}
