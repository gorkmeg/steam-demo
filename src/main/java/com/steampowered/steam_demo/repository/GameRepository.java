package com.steampowered.steam_demo.repository;

import com.steampowered.steam_demo.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    @Query("""
            select g from Game g
            where lower(g.name) like :pattern
                or lower(g.description) like :pattern
            """)
    Page<Game> searchForAdmin(@Param("pattern") String pattern, Pageable pageable);
}
