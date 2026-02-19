package com.steampowered.steam_demo.repository;

import com.steampowered.steam_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
