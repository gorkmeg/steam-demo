package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class LibraryItemNotFoundException extends ApiDomainException {
    public LibraryItemNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Library item not found");
    }
}
