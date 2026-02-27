package com.steampowered.steam_demo.mapper;

import com.steampowered.steam_demo.dto.request.GameCreateRequest;
import com.steampowered.steam_demo.entity.Game;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface GameMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    Game toEntity(GameCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    void updateEntity(GameCreateRequest request, @MappingTarget Game game);
}
