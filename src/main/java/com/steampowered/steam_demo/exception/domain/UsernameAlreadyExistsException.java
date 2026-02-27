package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends ApiDomainException {
    public UsernameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Username already exists");
    }
}
