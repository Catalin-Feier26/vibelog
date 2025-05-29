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
 * REST controller for managing the authenticated user's profile and public profile access.
 * <p>
 * All endpoints under <code>/api/users/me</code> derive the current user from the JWT token
 * in the {@link Authentication} principal. Public profiles can be fetched via <code>/api/users/{username}</code>.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final StorageService storageService;

    /**
     * Constructs the controller with required services.
     *
     * @param userService    service for profile operations
     * @param storageService service for file storage (e.g., avatars)
     */
    public UserController(UserService userService,
                          StorageService storageService) {
        this.userService    = userService;
        this.storageService = storageService;
    }

    /**
     * GET /api/users/me : Retrieve the profile of the currently authenticated user.
     *
     * @param auth the Spring Security {@link Authentication} containing the principal's username
     * @return a {@link ProfileResponse} with the user's profile information
     */
    @GetMapping("/me")
    public ProfileResponse getMyProfile(Authentication auth) {
        String username = auth.getName();
        return userService.getProfileByUsername(username);
    }

    /**
     * GET /api/users/{username} : Retrieve the public profile of any user by username.
     *
     * @param username the target user's username
     * @return a {@link ProfileResponse} with the public profile information
     */
    @GetMapping("/{username}")
    public ProfileResponse getUserProfile(@PathVariable String username) {
        return userService.getProfileByUsername(username);
    }

    /**
     * PUT /api/users/me : Update the profile of the currently authenticated user.
     * <p>
     * Only non-null fields in the {@link ProfileUpdateRequest} will be changed.
     * </p>
     *
     * @param auth the Spring Security {@link Authentication} containing the principal's username
     * @param req  the validated request body with fields to update
     * @return a {@link ProfileUpdateWithTokenResponse} containing a refreshed JWT and updated profile
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
     * POST /api/users/me/avatar : Upload a new profile picture for the current user.
     * <p>
     * Stores the file, updates the user's <code>profilePicture</code> URL,
     * and returns the updated profile.
     * </p>
     *
     * @param auth the Spring Security {@link Authentication} containing the principal's username
     * @param file the multipart file to upload
     * @return a {@link ProfileResponse} with the updated avatar URL
     */
    @PostMapping("/me/avatar")
    public ProfileResponse uploadAvatar(
            Authentication auth,
            @RequestParam("file") MultipartFile file
    ) {
        String username = auth.getName();
        String url = storageService.store(file);
        return userService.updateProfileByUsername(
                username,
                new ProfileUpdateRequest(null, null, null, url)
        );
    }

    /**
     * DELETE /api/users/me/avatar : Remove the current user's profile picture.
     * <p>
     * Deletes the stored file (if any) and clears the <code>profilePicture</code> field.
     * </p>
     *
     * @param auth the Spring Security {@link Authentication} containing the principal's username
     */
    @DeleteMapping("/me/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(Authentication auth) {
        String username = auth.getName();
        ProfileResponse before = userService.getProfileByUsername(username);
        if (before.profilePicture() != null) {
            storageService.delete(before.profilePicture());
        }
        userService.updateProfileByUsername(
                username,
                new ProfileUpdateRequest(null, null, null, null)
        );
    }
}
