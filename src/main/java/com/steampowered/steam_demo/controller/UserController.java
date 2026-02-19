package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.LoginRequest;
import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.LoginResponse;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public UserResponse me(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }
}
