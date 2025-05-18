package com.catalin.vibelog.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * Response returned after successful authentication (login or register).
 */
public record AuthResponse(
        String token,
        String tokenType,
        Instant expiresAt,
        Long userId,
        String username,
        String email,
        List<String> roles
) {}
