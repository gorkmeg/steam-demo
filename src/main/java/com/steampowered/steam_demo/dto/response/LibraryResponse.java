package com.steampowered.steam_demo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LibraryResponse {
    private UUID libraryItemId;
    private UUID userId;
    private UUID gameId;
    private String gameName;
    private String gameDescription;
    private BigDecimal gamePrice;
    private String gameType;
    private LocalDateTime addedAt;
}
