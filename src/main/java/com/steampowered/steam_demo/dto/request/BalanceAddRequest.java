package com.steampowered.steam_demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceAddRequest {
    @NotNull(message = "unaccepted balance type")
    @DecimalMin(value = "0.0", inclusive = false, message = "unaccepted balance type")
    private BigDecimal balance;
}
