package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.entity.Game;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.exception.domain.ApiDomainException;
import com.steampowered.steam_demo.repository.GameRepository;
import com.steampowered.steam_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

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
}
