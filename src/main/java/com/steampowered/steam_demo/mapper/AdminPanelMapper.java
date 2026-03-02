package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.response.AdminGameRowResponse;
import com.steampowered.steam_demo.dto.response.AdminUserRowResponse;
import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminPanelMapper {
    AdminUserRowResponse toAdminUserRowResponse(User user);
    AdminGameRowResponse toAdminGameRowResponse(Game game);
}
