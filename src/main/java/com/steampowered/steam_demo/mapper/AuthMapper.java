package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.response.LoginResponse;
import com.steampowered.steam_demo.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "expiresIn", source = "expiresIn")
    @Mapping(target = "user", source = "user")
    LoginResponse toLoginResponse(String token, long expiresIn, UserResponse user);
}
