package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.request.LoginRequest;
import com.steampowered.steam_demo.dto.response.LoginResponse;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.entity.UserType;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.repository.UserRepository;
import com.steampowered.steam_demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(UserType.ROLE_USER);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtService.generateToken(user));
        response.setExpiresIn(jwtService.getExpirationMs());
        response.setUser(userMapper.toResponse(user));
        return response;
    }
}
