package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.dto.request.LibraryAddRequest;
import com.steampowered.steam_demo.dto.response.LibraryResponse;
import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.entity.LibraryItem;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.exception.domain.GameNotFoundException;
import com.steampowered.steam_demo.exception.domain.LibraryItemNotFoundException;
import com.steampowered.steam_demo.exception.domain.UserNotFoundException;
import com.steampowered.steam_demo.mapper.LibraryMapper;
import com.steampowered.steam_demo.repository.GameRepository;
import com.steampowered.steam_demo.repository.LibraryRepository;
import com.steampowered.steam_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(GameNotFoundException::new);
        user.purchase(game.getPrice());

        LibraryItem libraryItem = libraryMapper.toEntity(request, user, game);
        return libraryMapper.toResponse(libraryRepository.save(libraryItem));
    }

    @Transactional(readOnly = true)
    public List<LibraryResponse> getUserLibrary(UUID userId) {
        return libraryRepository.findAllByUserId(userId)
                .stream()
                .map(libraryMapper::toResponse)
                .toList();
    }

    @Transactional
    public void refundGame(UUID userId, UUID libraryItemId) {
        LibraryItem libraryItem = libraryRepository.findByIdAndUserId(libraryItemId, userId)
                .orElseThrow(LibraryItemNotFoundException::new);

        User user = libraryItem.getUser();
        user.refund(libraryItem.refundAmount());

        libraryRepository.delete(libraryItem);
    }
}
