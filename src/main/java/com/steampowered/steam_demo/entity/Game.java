package com.steampowered.steam_demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalDateTime releaseDate = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private GameType gameType;
}
