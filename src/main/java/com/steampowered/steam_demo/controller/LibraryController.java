package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.LibraryAddRequest;
import com.steampowered.steam_demo.dto.response.LibraryResponse;
import com.steampowered.steam_demo.security.UserPrincipal;
import com.steampowered.steam_demo.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping
    public List<LibraryResponse> getMyLibrary(@AuthenticationPrincipal UserPrincipal principal) {
        return libraryService.getUserLibrary(principal.id());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryResponse addToMyLibrary(@RequestBody LibraryAddRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return libraryService.addGameToLibrary(principal.id(), request);
    }

    @DeleteMapping("/{libraryItemId}")
    public ResponseEntity<?> refundGame(@PathVariable UUID libraryItemId, @AuthenticationPrincipal UserPrincipal principal) {
        libraryService.refundGame(principal.id(), libraryItemId);
        return ResponseEntity.ok("Game refunded successfully");
    }
}
