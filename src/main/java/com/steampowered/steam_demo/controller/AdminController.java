package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.AdminUserUpdateRequest;
import com.steampowered.steam_demo.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @DeleteMapping("/delete-user")
    public void deleteUser(@RequestParam("id") UUID id) {
        adminService.deleteUser(id);
    }

    @DeleteMapping("/delete-game")
    public void deleteGame(@RequestParam("id") UUID id) {
        adminService.deleteGame(id);
    }

    @PutMapping("/update-user")
    public void updateUser(@Valid @RequestBody AdminUserUpdateRequest request) {
        adminService.updateUserDisplayName(request.getId(), request.getName());
    }
}
