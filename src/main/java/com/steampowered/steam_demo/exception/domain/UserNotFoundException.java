package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiDomainException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "User not found");
    }
}
