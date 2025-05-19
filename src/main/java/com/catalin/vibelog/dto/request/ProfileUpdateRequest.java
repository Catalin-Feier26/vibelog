package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating fields on an existing user's profile.
 * <p>
 * Any non-null field provided will overwrite the corresponding field on the {@code User} entity.
 * Fields left null will remain unchanged.
 * </p>
 *
 * @param email           the new email address to set; must be a valid format if provided
 * @param username        the new username; must be between 3 and 50 characters if provided
 * @param bio             a short biography; cannot exceed 500 characters if provided
 * @param profilePicture  URL or identifier of the new profile picture; handling of storage is external
 */
public record ProfileUpdateRequest(
        @Email(message = "Invalid email format")
        String email,

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        String bio,

        String profilePicture
) {}