package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.LoginRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.AuthResponse;
import com.catalin.vibelog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes endpoints for user authentication:
 * registration and login, returning JWT-based credentials.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * @param request the registration payload (email, username, password)
     * @return HTTP 201 with a token and user info in the body
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Logs in an existing user.
     *
     * @param request the login payload (email, password)
     * @return HTTP 200 with a token and user info in the body
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
