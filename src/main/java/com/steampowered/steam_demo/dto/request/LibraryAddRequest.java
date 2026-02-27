package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LibraryAddRequest {
    @NotNull(message = "gameId is required")
    private UUID gameId;
}
