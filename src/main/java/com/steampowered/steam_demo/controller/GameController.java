package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.GameCreateRequest;
import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/create-game")
    @ResponseStatus(HttpStatus.CREATED)
    public Game createGame(@Valid @RequestBody GameCreateRequest request) {
        return gameService.createGame(request);
    }

    @GetMapping
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    @GetMapping("/{id}")
    public Game getGameById(@PathVariable UUID id) {
        return gameService.getGameById(id);
    }
}
