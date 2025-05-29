package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.dto.response.ProfileUpdateWithTokenResponse;

import com.catalin.vibelog.security.JwtUtil;
import com.catalin.vibelog.service.StorageService;
import com.catalin.vibelog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for endpoints related to the authenticated user's profile.
 * <p>
 * All operations under <code>/api/users/me</code> derive the user from the JWT token.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService   userService;
    private final StorageService storageService;

    public UserController(UserService userService,
                          StorageService storageService) {
        this.userService    = userService;
        this.storageService = storageService;
    }

    /**
     * GET /api/users/me : Retrieve the profile of the currently authenticated user.
     *
     * @param auth Spring Security authentication containing the principal's username
     * @return a {@link ProfileResponse} with the user's profile information
     */
    @GetMapping("/me")
    public ProfileResponse getMyProfile(Authentication auth) {
        String username = auth.getName();
        return userService.getProfileByUsername(username);
    }
    /**
     * Get another user’s profile by username.
     *
     * @param username  the target username
     * @return the public ProfileResponse DTO
     */
    @GetMapping("/{username}")
    public ProfileResponse getUserProfile(@PathVariable String username) {
        return userService.getProfileByUsername(username);
    }
    /**
     * PUT /api/users/me : Update the profile of the currently authenticated user.
     * <p>
     * Only fields present (non-null) in the {@code ProfileUpdateRequest}
     * will be updated. Other fields remain unchanged.
     * </p>
     *
     * @param auth Spring Security authentication containing the principal's username
     * @param req  validated request body with fields to update
     * @return a {@link ProfileUpdateWithTokenResponse} reflecting the updated profile
     */
    @PutMapping("/me")
    public ProfileUpdateWithTokenResponse updateMyProfile(
            Authentication auth,
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        String username = auth.getName();
        return userService.updateProfileAndGetTokenByUsername(username, req);
    }

    /**
     * POST /api/users/me/avatar : upload a new profile picture
     */
    @PostMapping("/me/avatar")
    public ProfileResponse uploadAvatar(
            Authentication auth,
            @RequestParam("file") MultipartFile file
    ) {
        String username = auth.getName();
        // 1) store the file
        String url = storageService.store(file);
        // 2) update just the profilePicture field
        return userService.updateProfileByUsername(
                username,
                new ProfileUpdateRequest(null, null, null, url)
        );
    }

    /**
     * DELETE /api/users/me/avatar : remove profile picture
     */
    @DeleteMapping("/me/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(Authentication auth) {
        String username = auth.getName();
        // fetch current URL so we can clean up the file
        ProfileResponse before = userService.getProfileByUsername(username);
        if (before.profilePicture() != null) {
            storageService.delete(before.profilePicture());
        }
        // clear the field on the entity
        userService.updateProfileByUsername(
                username,
                new ProfileUpdateRequest(null, null, null, null)
        );
    }
}
