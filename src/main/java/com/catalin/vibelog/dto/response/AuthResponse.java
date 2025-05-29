package com.catalin.vibelog.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * Response returned after successful authentication (login or registration),
 * containing the JWT token and user details.
 *
 * @param token     the generated JWT token
 * @param tokenType the type of the token (e.g., "Bearer")
 * @param expiresAt the timestamp when the token will expire
 * @param userId    the ID of the authenticated user
 * @param username  the username of the authenticated user
 * @param email     the email of the authenticated user
 * @param roles     the list of roles assigned to the user
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
