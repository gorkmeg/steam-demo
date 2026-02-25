package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LibraryAddRequest {
    @NotNull(message = "Game ID must not be null")
    private UUID gameId;
}
