package com.steampowered.steam_demo.repository;

import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

    @Query("""
            select u from User u
            where lower(u.username) like :pattern
                or lower(u.displayName) like :pattern
            """)
    Page<User> searchForAdmin(@Param("pattern") String pattern, Pageable pageable);

    @Query("select u.status from User u where u.id = :id")
    Optional<UserStatus> findStatusById(@Param("id") UUID id);
}
