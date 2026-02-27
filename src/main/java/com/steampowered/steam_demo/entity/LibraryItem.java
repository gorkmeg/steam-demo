package com.steampowered.steam_demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "library_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_library_items_user_game", columnNames = {"user_id", "game_id"}))
public class LibraryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private BigDecimal purchasePrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    void prePersist() {
        addedAt = Objects.requireNonNullElse(addedAt, LocalDateTime.now());
    }

    public BigDecimal refundAmount() {
        return Objects.requireNonNullElse(purchasePrice, BigDecimal.ZERO);
    }
}
