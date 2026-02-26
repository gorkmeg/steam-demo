package com.steampowered.steam_demo.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String username,
        Collection<? extends GrantedAuthority> authorities
) {
}
