package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.dto.response.ProfileUpdateWithTokenResponse;

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

}
