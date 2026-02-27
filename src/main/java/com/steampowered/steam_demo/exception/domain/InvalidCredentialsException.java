package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiDomainException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
