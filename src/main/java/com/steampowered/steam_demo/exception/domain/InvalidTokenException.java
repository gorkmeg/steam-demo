package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiDomainException {
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid token");
    }
}
