package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.request.RegisterRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin‐only REST controller for full user management.
 * <p>
 * Secured with {@code @PreAuthorize("hasRole('ADMIN')")}, exposing CRUD operations
 * for user accounts, including listing, retrieval, creation, updates, and deletion.
 * </p>
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    /**
     * Constructs the controller with the required UserService.
     *
     * @param userService service providing user management operations
     */
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/admin/users?search={search}
     * <p>
     * List all users, optionally filtered by username fragment.
     * </p>
     *
     * @param usernameFragment optional substring to filter usernames (case-insensitive)
     * @param pageable         pagination and sorting information
     * @return a {@link Page} of {@link ProfileResponse} for matching users
     */
    @GetMapping
    public Page<ProfileResponse> list(
            @RequestParam(value = "search", required = false) String usernameFragment,
            Pageable pageable
    ) {
        return userService.listUsers(usernameFragment, pageable);
    }

    /**
     * GET /api/admin/users/{id}
     * <p>
     * Retrieve a single user's profile by ID.
     * </p>
     *
     * @param id the ID of the user to retrieve
     * @return a {@link ProfileResponse} for the specified user
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no user exists with the given ID
     */
    @GetMapping("/{id}")
    public ProfileResponse getOne(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * PUT /api/admin/users/{id}
     * <p>
     * Update any user's profile fields (email, username, bio, picture).
     * Only non-null fields in the request will be updated.
     * </p>
     *
     * @param id  the ID of the user to update
     * @param req the {@link ProfileUpdateRequest} containing fields to modify
     * @return the updated {@link ProfileResponse}
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no user exists with the given ID
     * @throws com.catalin.vibelog.exception.EmailAlreadyExistsException if the new email is already in use
     * @throws com.catalin.vibelog.exception.UsernameAlreadyExistsException if the new username is already in use
     */
    @PutMapping("/{id}")
    public ProfileResponse updateOne(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        return userService.updateUserById(id, req);
    }

    /**
     * DELETE /api/admin/users/{id}
     * <p>
     * Delete a user and cascade-delete their content.
     * </p>
     *
     * @param id the ID of the user to delete
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no user exists with the given ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    /**
     * POST /api/admin/users
     * <p>
     * Create a new user account (admin-only).
     * </p>
     *
     * @param req the {@link RegisterRequest} containing email, username, and password
     * @return the {@link ProfileResponse} of the newly created user
     * @throws com.catalin.vibelog.exception.EmailAlreadyExistsException    if the email is already registered
     * @throws com.catalin.vibelog.exception.UsernameAlreadyExistsException if the username is already taken
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createUser(
            @Valid @RequestBody RegisterRequest req
    ) {
        return userService.createUser(req);
    }
}
