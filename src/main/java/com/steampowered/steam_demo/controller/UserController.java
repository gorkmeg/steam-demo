package com.steampowered.steam_demo.controller;

import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

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
}
