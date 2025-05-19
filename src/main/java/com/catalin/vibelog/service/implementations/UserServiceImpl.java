package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.exception.EmailAlreadyExistsException;
import com.catalin.vibelog.exception.ResourceNotFoundException;
import com.catalin.vibelog.exception.UsernameAlreadyExistsException;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link UserService}, using Spring Data JPA for persistence.
 * All mutating methods are wrapped in transactions to ensure atomicity and rollback on failure.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * {@inheritDoc}
     * <p>This method is marked <code>readOnly=true</code> to optimize for non-mutative queries.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + userId));
        return toDto(u);
    }

    /**
     * {@inheritDoc}
     * <p>Updates only fields provided in the request. Runs within a transaction.</p>
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return toDto(u);
    }

    /**
     * {@inheritDoc}
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
     * Applies non-null updates from the request to the given {@code User} entity.
     *
     * @param u   the {@link User} to modify
     * @param req the {@link ProfileUpdateRequest} containing fields to apply
     * @throws EmailAlreadyExistsException    if attempting to set an email that's already in use
     * @throws UsernameAlreadyExistsException if attempting to set a username that's already in use
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
     * Maps a {@link User} entity to a {@link ProfileResponse} DTO.
     *
     * @param u the user entity to convert
     * @return the populated response DTO
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