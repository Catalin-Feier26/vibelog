package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for registering a new user account.
 * <p>
 * Contains the necessary fields for creating a new user:
 * email, username, and password. Validation annotations enforce
 * non-blank values and a proper email format.
 * </p>
 *
 * @param email    the user's email address; must be a valid, non-blank email
 * @param username the desired username; must be non-blank
 * @param password the raw password; must be non-blank
 */
public record RegisterRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String username,

        @NotBlank
        String password
) {}
