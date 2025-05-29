package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for authenticating an existing user.
 * <p>
 * Contains the credentials required for login: email and password.
 * Validation annotations enforce a valid, non-blank email and a non-blank password.
 * </p>
 *
 * @param email    the user's email address; must be a valid, non-blank email
 * @param password the user's password; must be non-blank
 */
public record LoginRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {}
