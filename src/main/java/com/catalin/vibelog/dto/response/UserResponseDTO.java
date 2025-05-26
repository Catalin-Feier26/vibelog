package com.catalin.vibelog.dto.response;

/**
 * Minimal info about a user for search results.
 *
 * @param id               the user’s database ID
 * @param username         their unique username
 * @param bio              their short biography (may be blank)
 * @param profilePicture   URL/path to their avatar (may be null)
 */
public record UserResponseDTO(
        Long   id,
        String username,
        String bio,
        String profilePicture
) {}
