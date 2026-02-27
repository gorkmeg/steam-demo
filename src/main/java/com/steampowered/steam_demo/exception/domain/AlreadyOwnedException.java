package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class AlreadyOwnedException extends ApiDomainException {
    public AlreadyOwnedException() {
        super(HttpStatus.CONFLICT, "Game already exists in library");
    }
}
