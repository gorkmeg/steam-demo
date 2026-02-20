package com.steampowered.steam_demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceAddRequest {
    private BigDecimal balance;
}
