package com.steampowered.steam_demo.exception.domain;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends ApiDomainException {
    public InsufficientBalanceException() {
        super(HttpStatus.CONFLICT, "Insufficient balance");
    }
}
