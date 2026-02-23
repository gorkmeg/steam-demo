package com.steampowered.steam_demo.repository;

import com.steampowered.steam_demo.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LibraryRepository extends JpaRepository<LibraryItem, UUID> {
    List<LibraryItem> findAllByUserId(UUID userId);
    boolean existsByUserIdAndGameId(UUID userId, UUID gameId);
    Optional<LibraryItem> findByUserIdAndGameId(UUID userId, UUID gameId);
    Optional<LibraryItem> findByIdAndUserId(UUID id, UUID userId);
}
