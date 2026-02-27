package com.steampowered.steam_demo.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiDomainException extends RuntimeException {
    private final HttpStatus status;

    public ApiDomainException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
