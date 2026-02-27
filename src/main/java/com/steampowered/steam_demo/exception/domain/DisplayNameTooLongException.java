package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class DisplayNameTooLongException extends ApiDomainException {
    public DisplayNameTooLongException() {
        super(HttpStatus.BAD_REQUEST, "Display name too long");
    }
}
