package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userType", ignore = true)
    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);
}
