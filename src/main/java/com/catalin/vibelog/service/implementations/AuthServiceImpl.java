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
 * Default implementation of {@link AuthService}, handling
 * registration and login flows.
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

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

        // 5) build response
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

    @Override
    public AuthResponse login(LoginRequest dto) {
        // 1) fetch user
        User user = userRepo.findByEmail(dto.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // 2) verify password
        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // 3) issue JWT
        String token = jwtUtil.generateToken(user);
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtUtil.getJwtExpirationInMs());

        // 4) return response
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
