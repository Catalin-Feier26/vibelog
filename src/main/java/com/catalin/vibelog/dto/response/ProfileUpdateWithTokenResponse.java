package com.catalin.vibelog.dto.response;

/**
 * Response DTO that bundles a freshly minted JWT with the updated user profile information.
 */
public record ProfileUpdateWithTokenResponse(
        /**
         * Newly generated JWT token for the authenticated user.
         */
        String token,

        /**
         * {@link ProfileResponse} containing the updated profile details.
         */
        ProfileResponse profile
) {
}
