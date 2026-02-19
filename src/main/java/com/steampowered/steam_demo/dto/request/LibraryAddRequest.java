package com.steampowered.steam_demo.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class LibraryAddRequest {
    private UUID gameId;
}
