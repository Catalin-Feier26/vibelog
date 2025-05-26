package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.dto.response.ProfileUpdateWithTokenResponse;
import com.catalin.vibelog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface defining operations related to retrieving and updating user profiles.
 */
public interface UserService {

    /**
     * Retrieve a user's profile by their unique identifier.
     *
     * @param userId the ID of the user to look up
     * @return a {@link ProfileResponse} containing the user's profile data
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no user exists with the given ID
     */
    ProfileResponse getProfile(Long userId);

    /**
     * Apply partial updates to a user's profile by ID.
     * Only non-null values in the request will be applied.
     *
     * @param userId the ID of the user to update
     * @param req    the {@link ProfileUpdateRequest} containing fields to update
     * @return a {@link ProfileResponse} reflecting the updated profile
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException     if no user exists with the given ID
     * @throws com.catalin.vibelog.exception.EmailAlreadyExistsException   if the new email is already in use
     * @throws com.catalin.vibelog.exception.UsernameAlreadyExistsException if the new username is already in use
     */
    ProfileResponse updateProfile(Long userId, ProfileUpdateRequest req);

    /**
     * Retrieve the current authenticated user's profile by their username.
     *
     * @param username the username of the authenticated user
     * @return a {@link ProfileResponse} containing the user's profile data
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no user exists with the given username
     */
    ProfileResponse getProfileByUsername(String username);

    /**
     * Apply partial updates to the current authenticated user's profile by username.
     * Only non-null values in the request will be applied.
     *
     * @param username the username of the authenticated user
     * @param req      the {@link ProfileUpdateRequest} containing fields to update
     * @return a {@link ProfileResponse} reflecting the updated profile
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException     if no user exists with the given username
     * @throws com.catalin.vibelog.exception.EmailAlreadyExistsException   if the new email is already in use
     * @throws com.catalin.vibelog.exception.UsernameAlreadyExistsException if the new username is already in use
     */
    ProfileResponse updateProfileByUsername(String username, ProfileUpdateRequest req);

    /**
     * Update the authenticated user's profile _and_ return
     * a new JWT based on the updated User entity.
     *
     * @param username the current username (from the JWT)
     * @param req      the profile‐update request
     * @return a {@link ProfileUpdateWithTokenResponse} with both token and profile
     */
    ProfileUpdateWithTokenResponse updateProfileAndGetTokenByUsername(String username, ProfileUpdateRequest req);
    /**
     * Lookup a User by username.
     *
     * @param username the username to search for
     * @return the matching User
     * @throws jakarta.persistence.EntityNotFoundException if no user with that username exists
     */
    User findByUsername(String username);
    /**
     * Return a paginated list of all users, optionally filtering by username fragment.
     */
    Page<ProfileResponse> listUsers(String usernameFragment, Pageable page);

    /**
     * Fetch a single user by their numerical ID.
     */
    ProfileResponse getUserById(Long userId);

    /**
     * Update any user's profile by ID (email, username, bio, picture).
     */
    ProfileResponse updateUserById(Long userId, ProfileUpdateRequest req);

    /**
     * Delete a user (and cascade‐delete their content).
     */
    void deleteUserById(Long userId);
    /**
     * Create a brand-new user (admin-only).
     *
     * @param req carries email, username, and raw password
     * @return the profile of the newly created user
     */
    ProfileResponse createUser(RegisterRequest req);
}
