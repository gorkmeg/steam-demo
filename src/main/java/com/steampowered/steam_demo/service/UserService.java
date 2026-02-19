package com.steampowered.steam_demo.service;

import com.steampowered.steam_demo.dto.request.RegisterRequest;
import com.steampowered.steam_demo.entity.User;
import com.steampowered.steam_demo.entity.UserType;
import com.steampowered.steam_demo.mapper.UserMapper;
import com.steampowered.steam_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(PASSWORD_ENCODER.encode(request.getPassword()));
        user.setUserType(UserType.ROLE_USER);

        return userRepository.save(user);
    }
}
