package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.dto.request.LibraryAddRequest;
import com.steampowered.steam_demo.dto.response.LibraryResponse;
import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.entity.LibraryItem;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.mapper.LibraryMapper;
import com.steampowered.steam_demo.repository.GameRepository;
import com.steampowered.steam_demo.repository.LibraryRepository;
import com.steampowered.steam_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final LibraryMapper libraryMapper;

    @Transactional
    public LibraryResponse addGameToLibrary(UUID userId, LibraryAddRequest request) {
        if (request == null || request.getGameId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gameId is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (libraryRepository.existsByUserIdAndGameId(userId, game.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game already exists in library");
        }

        if(user.getBalance().compareTo(game.getPrice()) < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(game.getPrice()));

        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setUser(user);
        libraryItem.setGame(game);

        return libraryMapper.toResponse(libraryRepository.save(libraryItem));
    }

    @Transactional(readOnly = true)
    public List<LibraryResponse> getUserLibrary(UUID userId) {
        return libraryRepository.findAllByUserId(userId)
                .stream()
                .map(libraryMapper::toResponse)
                .toList();
    }
}
