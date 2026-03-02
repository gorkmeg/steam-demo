package com.steampowered.steam_demo.dto.response;

import com.steampowered.steam_demo.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminUserRowResponse {
    private final UUID id;
    private final String username;
    private final String displayName;
    private final UserType userType;
    private final BigDecimal balance;
}
