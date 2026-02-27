package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.dto.request.DisplayNameUpdateRequest;
import com.steampowered.steam_demo.dto.request.LoginRequest;
import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.dto.response.LoginResponse;
import com.steampowered.steam_demo.dto.response.UserResponse;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.exception.domain.InvalidCredentialsException;
import com.steampowered.steam_demo.exception.domain.InvalidTokenException;
import com.steampowered.steam_demo.exception.domain.UserNotFoundException;
import com.steampowered.steam_demo.mapper.AuthMapper;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.repository.UserRepository;
import com.steampowered.steam_demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public User createUser(RegisterRequest request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return authMapper.toLoginResponse(
                jwtService.generateToken(user),
                jwtService.getExpirationMs(),
                userMapper.toResponse(user)
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(InvalidTokenException::new);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void addBalance(UUID userId, BigDecimal amount){
        User user = findUserOrThrow(userId);
        user.addBalance(amount);
    }

    @Transactional
    public void updateDisplayName(UUID userId, DisplayNameUpdateRequest request) {
        User user = findUserOrThrow(userId);
        userMapper.updateDisplayName(request, user);
        user.changeDisplayName(user.getDisplayName());
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
