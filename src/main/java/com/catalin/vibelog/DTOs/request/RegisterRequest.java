package com.catalin.vibelog.DTOs.request;

/**
 * Payload for registering a new user.
 */
public record RegisterRequest(
        @jakarta.validation.constraints.Email
        @jakarta.validation.constraints.NotBlank
        String email,

        @jakarta.validation.constraints.NotBlank
        String username,

        @jakarta.validation.constraints.NotBlank
        String password
) {}
