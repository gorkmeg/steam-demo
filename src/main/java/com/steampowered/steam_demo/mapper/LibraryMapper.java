package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.request.LibraryAddRequest;
import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.dto.response.LibraryResponse;
import com.steampowered.steam_demo.entity.LibraryItem;
import com.steampowered.steam_demo.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LibraryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "game", source = "game")
    @Mapping(target = "purchasePrice", source = "game.price")
    LibraryItem toEntity(LibraryAddRequest request, User user, Game game);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "purchasePrice", ignore = true)
    void updateEntity(LibraryAddRequest request, @MappingTarget LibraryItem libraryItem);

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
