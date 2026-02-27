package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.request.DisplayNameUpdateRequest;
import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userType", ignore = true)
    @Mapping(target = "balance", ignore = true)
    User toEntity(RegisterRequest request);

    @BeanMapping(
            ignoreByDefault = true,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "displayName", expression = "java(request.getDisplayName().trim())")
    void updateDisplayName(DisplayNameUpdateRequest request, @MappingTarget User user);

    UserResponse toResponse(User user);
}
