package com.steampowered.steam_demo.dto.response;

import com.steampowered.steam_demo.entity.UserType;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private UserType userType;
}
