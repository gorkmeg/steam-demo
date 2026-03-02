package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.AdminUserUpdateRequest;
import com.steampowered.steam_demo.dto.response.AdminGameRowResponse;
import com.steampowered.steam_demo.dto.response.AdminUserRowResponse;
import com.steampowered.steam_demo.dto.response.PagedResponse;
import com.steampowered.steam_demo.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public PagedResponse<AdminUserRowResponse> listUsers(
            @RequestParam(value = "q", required = false) String query,
            @PageableDefault(size = 20, sort = "username", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return PagedResponse.from(adminService.listUsers(query, pageable));
    }

    @GetMapping("/games")
    public PagedResponse<AdminGameRowResponse> listGames(
            @RequestParam(value = "q", required = false) String query,
            @PageableDefault(size = 20, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return PagedResponse.from(adminService.listGames(query, pageable));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
    }

    @DeleteMapping("/games/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable UUID id) {
        adminService.deleteGame(id);
    }

    @DeleteMapping("/delete-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserLegacy(@RequestParam("id") UUID id) {
        adminService.deleteUser(id);
    }

    @DeleteMapping("/delete-game")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGameLegacy(@RequestParam("id") UUID id) {
        adminService.deleteGame(id);
    }

    @PutMapping("/update-user")
    public void updateUser(@Valid @RequestBody AdminUserUpdateRequest request) {
        adminService.updateUserDisplayName(request.getId(), request.getName());
    }
}
