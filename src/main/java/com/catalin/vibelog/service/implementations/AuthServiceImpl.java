package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.LoginRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.AuthResponse;
import com.catalin.vibelog.exception.EmailAlreadyExistsException;
import com.catalin.vibelog.exception.InvalidCredentialsException;
import com.catalin.vibelog.exception.UsernameAlreadyExistsException;
import com.catalin.vibelog.factory.UserFactory;
import com.catalin.vibelog.factory.UserFactoryProvider;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.security.JwtUtil;
import com.catalin.vibelog.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Default implementation of {@link AuthService}, handling user registration and authentication.
 * <p>
 * Provides methods to register new users and authenticate existing users by issuing JWT tokens.
 * </p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructs the AuthService implementation with required dependencies.
     *
     * @param userRepo        repository for user persistence and lookup
     * @param passwordEncoder encoder for hashing and verifying passwords
     * @param jwtUtil         utility for generating JWT tokens
     */
    public AuthServiceImpl(UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user account.
     * <p>
     * Validates that the email and username are unique, hashes the user's password,
     * persists the new user, and issues a JWT token for immediate authentication.
     * </p>
     *
     * @param dto the {@link RegisterRequest} containing registration details: email, password, and username
     * @return an {@link AuthResponse} containing the JWT token, token type, expiration, and user details
     * @throws EmailAlreadyExistsException    if the email is already registered
     * @throws UsernameAlreadyExistsException if the username is already taken
     */
    @Override
    public AuthResponse register(RegisterRequest dto) {
        if (userRepo.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email already in use: " + dto.email());
        }
        if (userRepo.existsByUsername(dto.username())) {
            throw new UsernameAlreadyExistsException("Username already in use: " + dto.username());
        }

        String hash = passwordEncoder.encode(dto.password());

        UserFactory factory = UserFactoryProvider.getFactory(Role.USER);
        User user = factory.create(dto.email(), hash, dto.username());

        userRepo.save(user);

        String token = jwtUtil.generateToken(user);
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtUtil.getJwtExpirationInMs());

        return new AuthResponse(
                token,
                "Bearer",
                expiresAt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                List.of(user.getRole().name())
        );
    }

    /**
     * Authenticate an existing user and issue a JWT token.
     * <p>
     * Validates the provided credentials (email and password); if valid,
     * issues a JWT token for the user.
     * </p>
     *
     * @param dto the {@link LoginRequest} containing login credentials: email and password
     * @return an {@link AuthResponse} containing the JWT token, token type, expiration, and user details
     * @throws InvalidCredentialsException if the email is not registered or the password is incorrect
     */
    @Override
    public AuthResponse login(LoginRequest dto) {
        User user = userRepo.findByEmail(dto.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtUtil.getJwtExpirationInMs());

        return new AuthResponse(
                token,
                "Bearer",
                expiresAt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                List.of(user.getRole().name())
        );
    }
}
