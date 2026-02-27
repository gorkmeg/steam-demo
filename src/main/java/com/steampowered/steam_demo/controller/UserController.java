package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.BalanceAddRequest;
import com.steampowered.steam_demo.dto.request.DisplayNameUpdateRequest;
import com.steampowered.steam_demo.dto.request.LoginRequest;
import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.LoginResponse;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.security.UserPrincipal;
import com.steampowered.steam_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody RegisterRequest registerRequest) {
        User createdUser = userService.createUser(registerRequest);
        return userMapper.toResponse(createdUser);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getCurrentUser(principal.id());
    }

    @PostMapping("/add-balance")
    public ResponseEntity<?> addBalance(
            @RequestBody BalanceAddRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ){
        if(request.getBalance() == null || request.getBalance().compareTo(BigDecimal.ZERO) <= 0){
            return ResponseEntity.badRequest().body("unaccepted balance type");
        }

        userService.addBalance(principal.id(), request.getBalance());

        return ResponseEntity.ok("successful");

    }

    @PutMapping("/update-display-name")
    public ResponseEntity<?> updateDisplayName(@RequestBody
        DisplayNameUpdateRequest request,
        @AuthenticationPrincipal UserPrincipal principal){
        userService.updateDisplayName(principal.id(), request.getDisplayName());
        return ResponseEntity.ok("display name updated");
    }
}
