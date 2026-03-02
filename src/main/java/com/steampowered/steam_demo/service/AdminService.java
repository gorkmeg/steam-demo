package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.exception.domain.ApiDomainException;
import com.steampowered.steam_demo.repository.GameRepository;
import com.steampowered.steam_demo.repository.UserRepository;
import com.steampowered.steam_demo.dto.response.AdminGameRowResponse;
import com.steampowered.steam_demo.dto.response.AdminUserRowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Transactional(readOnly = true)
    public Page<AdminUserRowResponse> listUsers(String query, Pageable pageable) {
        Pageable safePageable = toSafePageable(pageable);
        String normalizedQuery = normalizeQuery(query);

        Page<User> usersPage = normalizedQuery == null
                ? userRepository.findAll(safePageable)
                : userRepository.searchForAdmin(toLikePattern(normalizedQuery), safePageable);

        return usersPage
                .map(user -> new AdminUserRowResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getUserType(),
                        user.getBalance()
                ));
    }

    @Transactional(readOnly = true)
    public Page<AdminGameRowResponse> listGames(String query, Pageable pageable) {
        Pageable safePageable = toSafePageable(pageable);
        String normalizedQuery = normalizeQuery(query);

        Page<Game> gamesPage = normalizedQuery == null
                ? gameRepository.findAll(safePageable)
                : gameRepository.searchForAdmin(toLikePattern(normalizedQuery), safePageable);

        return gamesPage
                .map(game -> new AdminGameRowResponse(
                        game.getId(),
                        game.getName(),
                        game.getDescription(),
                        game.getPrice(),
                        game.getGameType(),
                        game.getReleaseDate()
                ));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiDomainException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteGame(UUID id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ApiDomainException(HttpStatus.NOT_FOUND, "Game not found"));
        gameRepository.delete(game);
    }

    @Transactional
    public void updateUserDisplayName(UUID id, String name) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiDomainException(HttpStatus.NOT_FOUND, "User not found"));
        user.changeDisplayName(name);
    }

    private Pageable toSafePageable(Pageable pageable) {
        int pageNumber = Math.max(0, pageable.getPageNumber());
        int pageSize = Math.min(Math.max(1, pageable.getPageSize()), MAX_PAGE_SIZE);
        return PageRequest.of(pageNumber, pageSize, pageable.getSort());
    }

    private String normalizeQuery(String query) {
        if (query == null) {
            return null;
        }
        String trimmed = query.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String toLikePattern(String query) {
        return "%" + query.toLowerCase() + "%";
    }
}
