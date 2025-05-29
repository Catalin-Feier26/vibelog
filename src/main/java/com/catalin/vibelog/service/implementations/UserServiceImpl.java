package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.dto.response.ProfileUpdateWithTokenResponse;
import com.catalin.vibelog.exception.EmailAlreadyExistsException;
import com.catalin.vibelog.exception.ResourceNotFoundException;
import com.catalin.vibelog.exception.UsernameAlreadyExistsException;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.security.JwtUtil;
import com.catalin.vibelog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link UserService}, backed by Spring Data JPA.
 * <p>
 * Provides transactional user management operations including profile retrieval,
 * partial updates, token regeneration, pagination, and administrative user creation.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the service with required dependencies.
     *
     * @param userRepo        repository for user persistence
     * @param jwtUtil         utility for JWT generation
     * @param passwordEncoder encoder for hashing user passwords
     */
    public UserServiceImpl(UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieve a user's profile by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a {@link ProfileResponse} dto of the found user
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + userId));
        return toDto(u);
    }

    /**
     * Apply partial updates to a user's profile by ID.
     * Only non-null fields in the request are applied.
     *
     * @param userId the ID of the user to update
     * @param req    the changes to apply
     * @return the updated {@link ProfileResponse}
     * @throws ResourceNotFoundException      if no user exists with the given ID
     * @throws EmailAlreadyExistsException    if the new email is already in use
     * @throws UsernameAlreadyExistsException if the new username is already in use
     */
    @Override
    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + userId));
        applyUpdates(u, req);
        return toDto(userRepo.save(u));
    }

    /**
     * Retrieve a user's profile by username.
     *
     * @param username the username of the user to retrieve
     * @return a {@link ProfileResponse} dto of the found user
     * @throws ResourceNotFoundException if no user exists with the given username
     */
    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return toDto(u);
    }

    /**
     * Apply partial updates to a user's profile by username.
     * Only non-null fields in the request are applied.
     *
     * @param username the username of the user to update
     * @param req      the changes to apply
     * @return the updated {@link ProfileResponse}
     * @throws ResourceNotFoundException      if no user exists with the given username
     * @throws EmailAlreadyExistsException    if the new email is already in use
     * @throws UsernameAlreadyExistsException if the new username is already in use
     */
    @Override
    @Transactional
    public ProfileResponse updateProfileByUsername(String username, ProfileUpdateRequest req) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        applyUpdates(u, req);
        return toDto(userRepo.save(u));
    }

    /**
     * Update user profile and generate a new JWT for the updated identity.
     *
     * @param username the current username (extracted from existing JWT)
     * @param req      the changes to apply
     * @return a {@link ProfileUpdateWithTokenResponse} containing refreshed token and updated profile
     * @throws ResourceNotFoundException      if no user exists with the given username
     * @throws EmailAlreadyExistsException    if the new email is already in use
     * @throws UsernameAlreadyExistsException if the new username is already in use
     */
    @Override
    @Transactional
    public ProfileUpdateWithTokenResponse updateProfileAndGetTokenByUsername(
            String username,
            ProfileUpdateRequest req
    ) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        applyUpdates(u, req);
        User saved = userRepo.save(u);

        String newToken = jwtUtil.generateToken(saved);
        ProfileResponse profile = toDto(saved);
        return new ProfileUpdateWithTokenResponse(newToken, profile);
    }

    /**
     * Lookup a user entity by username.
     *
     * @param username the username to search
     * @return the found {@link User}
     * @throws EntityNotFoundException if no user exists with the given username
     */
    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + username)
                );
    }

    /**
     * List users with optional username filtering in a paginated view.
     *
     * @param usernameFragment filter substring for usernames (ignored if blank)
     * @param page             pagination and sorting information
     * @return a {@link Page} of {@link ProfileResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponse> listUsers(String usernameFragment, Pageable page) {
        if (usernameFragment != null && !usernameFragment.isBlank()) {
            return userRepo
                    .findByUsernameContainingIgnoreCase(usernameFragment, page)
                    .map(this::toDto);
        }
        return userRepo.findAll(page).map(this::toDto);
    }

    /**
     * Retrieve a user's profile by ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a {@link ProfileResponse} dto
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getUserById(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + userId));
        return toDto(u);
    }

    /**
     * Partial update of any user's profile by ID (admin operation).
     *
     * @param userId the ID of the user to update
     * @param req    the changes to apply
     * @return the updated {@link ProfileResponse}
     * @throws ResourceNotFoundException      if no user exists with the given ID
     * @throws EmailAlreadyExistsException    if the new email is already in use
     * @throws UsernameAlreadyExistsException if the new username is already in use
     */
    @Override
    @Transactional
    public ProfileResponse updateUserById(Long userId, ProfileUpdateRequest req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + userId));
        applyUpdates(u, req);
        return toDto(userRepo.save(u));
    }

    /**
     * Delete a user by ID along with associated data.
     *
     * @param userId the ID of the user to delete
     * @throws ResourceNotFoundException if no user exists with the given ID
     */
    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: id=" + userId);
        }
        userRepo.deleteById(userId);
    }

    /**
     * Create a new regular user (admin-only operation).
     * Validates uniqueness, hashes password, and assigns USER role.
     *
     * @param req registration data containing email, username, and raw password
     * @return the {@link ProfileResponse} dto of the newly created user
     * @throws EmailAlreadyExistsException    if the email is already registered
     * @throws UsernameAlreadyExistsException if the username is already taken
     */
    @Override
    @Transactional
    public ProfileResponse createUser(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException("Email already in use: " + req.email());
        }
        if (userRepo.existsByUsername(req.username())) {
            throw new UsernameAlreadyExistsException("Username already in use: " + req.username());
        }

        User u = new RegularUser();
        u.setEmail(req.email());
        u.setUsername(req.username());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(Role.USER);
        User saved = userRepo.save(u);

        return toDto(saved);
    }

    /**
     * Apply non-null updates from the request to the given user entity,
     * enforcing uniqueness for email and username.
     *
     * @param u   the user entity to modify
     * @param req contains new values to apply
     * @throws EmailAlreadyExistsException    if the new email is already in use
     * @throws UsernameAlreadyExistsException if the new username is already in use
     */
    private void applyUpdates(User u, ProfileUpdateRequest req) {
        if (req.email() != null && !req.email().equals(u.getEmail())) {
            if (userRepo.existsByEmail(req.email())) {
                throw new EmailAlreadyExistsException("Email already in use: " + req.email());
            }
            u.setEmail(req.email());
        }
        if (req.username() != null && !req.username().equals(u.getUsername())) {
            if (userRepo.existsByUsername(req.username())) {
                throw new UsernameAlreadyExistsException("Username already in use: " + req.username());
            }
            u.setUsername(req.username());
        }
        if (req.bio() != null) {
            u.setBio(req.bio());
        }
        if (req.profilePicture() != null) {
            u.setProfilePicture(req.profilePicture());
        }
    }

    /**
     * Map a {@link User} entity to a {@link ProfileResponse} dto.
     *
     * @param u the user entity
     * @return the corresponding response dto
     */
    private ProfileResponse toDto(User u) {
        return new ProfileResponse(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                u.getBio(),
                u.getProfilePicture(),
                u.getCreatedAt(),
                List.of(u.getRole().name())
        );
    }
}