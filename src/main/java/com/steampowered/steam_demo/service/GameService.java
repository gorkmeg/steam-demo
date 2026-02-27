package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.dto.request.GameCreateRequest;
import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.exception.domain.GameNotFoundException;
import com.steampowered.steam_demo.mapper.GameMapper;
import com.steampowered.steam_demo.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    @Transactional
    public Game createGame(GameCreateRequest request) {
        Game game = gameMapper.toEntity(request);
        return gameRepository.save(game);
    }

    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Game getGameById(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }
}
