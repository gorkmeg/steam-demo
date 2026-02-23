package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.response.LibraryResponse;
import com.steampowered.steam_demo.entity.LibraryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryMapper {
    @Mapping(target = "libraryItemId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "gameId", source = "game.id")
    @Mapping(target = "gameName", source = "game.name")
    @Mapping(target = "gameDescription", source = "game.description")
    @Mapping(target = "gamePrice", source = "purchasePrice")
    @Mapping(target = "gameType", source = "game.gameType")
    @Mapping(target = "addedAt", source = "addedAt")
    LibraryResponse toResponse(LibraryItem libraryItem);
}
