package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class GameNotFoundException extends ApiDomainException {
    public GameNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Game not found");
    }

    public GameNotFoundException(UUID gameId) {
        super(HttpStatus.NOT_FOUND, "Game not found: " + gameId);
    }
}
