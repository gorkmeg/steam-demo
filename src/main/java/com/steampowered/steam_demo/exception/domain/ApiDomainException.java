package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public abstract class ApiDomainException extends RuntimeException {
    private final HttpStatus status;

    protected ApiDomainException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
