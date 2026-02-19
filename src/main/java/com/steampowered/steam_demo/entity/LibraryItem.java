package com.steampowered.steam_demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "library_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
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
}
