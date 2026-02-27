package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.BalanceAddRequest;
import com.steampowered.steam_demo.dto.request.DisplayNameUpdateRequest;
import com.steampowered.steam_demo.dto.request.LoginRequest;
import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.LoginResponse;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.mapper.BalanceMapper;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.security.UserPrincipal;
import com.steampowered.steam_demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final BalanceMapper balanceMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User createdUser = userService.createUser(registerRequest);
        return userMapper.toResponse(createdUser);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getCurrentUser(principal.id());
    }

    @PostMapping("/add-balance")
    public ResponseEntity<?> addBalance(
            @Valid @RequestBody BalanceAddRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ){
        userService.addBalance(principal.id(), balanceMapper.toAmount(request));
        return ResponseEntity.ok("successful");
    }

    @PutMapping("/update-display-name")
    public ResponseEntity<?> updateDisplayName(@Valid @RequestBody
        DisplayNameUpdateRequest request,
        @AuthenticationPrincipal UserPrincipal principal){
        userService.updateDisplayName(principal.id(), request);
        return ResponseEntity.ok("display name updated");
    }
}
