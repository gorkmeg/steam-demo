package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.LibraryAddRequest;
import com.steampowered.steam_demo.dto.response.LibraryResponse;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.service.LibraryService;
import com.steampowered.steam_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;
    private final UserService userService;

    @GetMapping
    public List<LibraryResponse> getMyLibrary(Authentication authentication) {
        UserResponse currentUser = userService.getCurrentUser(authentication.getName());
        return libraryService.getUserLibrary(currentUser.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryResponse addToMyLibrary(@RequestBody LibraryAddRequest request, Authentication authentication) {
        UserResponse currentUser = userService.getCurrentUser(authentication.getName());
        return libraryService.addGameToLibrary(currentUser.getId(), request);
    }
}
