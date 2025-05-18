package com.catalin.vibelog.dto.request;

/**
 * Payload for logging in an existing user.
 */
public record LoginRequest(
        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        String email,

        @jakarta.validation.constraints.NotBlank
        String password
) {}