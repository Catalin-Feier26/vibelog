package com.catalin.vibelog.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO representing a user's profile information returned by the API.
 *
 * @param id              unique identifier of the user
 * @param email           email address of the user
 * @param username        username chosen by the user
 * @param bio             short biography or description
 * @param profilePicture  URL or identifier of the user's profile picture
 * @param createdAt       timestamp when the user account was created
 * @param roles           list of roles granted to the user (e.g., USER, ADMIN)
 */
public record ProfileResponse(
        Long id,
        String email,
        String username,
        String bio,
        String profilePicture,
        LocalDateTime createdAt,
        List<String> roles
) {}