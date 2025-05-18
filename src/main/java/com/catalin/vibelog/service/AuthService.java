package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.LoginRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.AuthResponse;

/**
 * Authentication operations: register new users and log in existing ones.
 */
public interface AuthService {

    /**
     * Register a new user account.
     *
     * @param dto the registration payload
     * @return an auth response containing JWT and user info
     * @throws com.catalin.vibelog.exception.EmailAlreadyExistsException    if email is taken
     * @throws com.catalin.vibelog.exception.UsernameAlreadyExistsException if username is taken
     */
    AuthResponse register(RegisterRequest dto);

    /**
     * Log in an existing user.
     *
     * @param dto the login payload
     * @return an auth response containing JWT and user info
     * @throws com.catalin.vibelog.exception.InvalidCredentialsException if email/password don’t match
     */
    AuthResponse login(LoginRequest dto);
}
