package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.ProfileUpdateRequest;
import com.catalin.vibelog.dto.response.ProfileResponse;
import com.catalin.vibelog.dto.response.ProfileUpdateWithTokenResponse;
import com.catalin.vibelog.security.JwtUtil;
import com.catalin.vibelog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for endpoints related to the authenticated user's profile.
 * <p>
 * All operations under <code>/api/users/me</code> derive the user from the JWT token.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;


    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil=jwtUtil;
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
}
