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
 * Admin‐only endpoints for full user management.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /** GET /api/admin/users?search=foo */
    @GetMapping
    public Page<ProfileResponse> list(
            @RequestParam(value="search", required=false) String usernameFragment,
            Pageable pageable
    ) {
        return userService.listUsers(usernameFragment, pageable);
    }

    /** GET /api/admin/users/{id} */
    @GetMapping("/{id}")
    public ProfileResponse getOne(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /** PUT /api/admin/users/{id} */
    @PutMapping("/{id}")
    public ProfileResponse updateOne(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
        return userService.updateUserById(id, req);
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
    /**
     * POST /api/admin/users
     * <p>Create a new user account.</p>
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createUser(
            @Valid @RequestBody RegisterRequest req
    ) {
        return userService.createUser(req);
    }
}
