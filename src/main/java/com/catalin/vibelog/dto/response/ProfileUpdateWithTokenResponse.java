package com.catalin.vibelog.dto.response;
/**
 * Bundles a freshly minted JWT with the updated profile info.
 */
public record ProfileUpdateWithTokenResponse(
        String token,
        ProfileResponse profile
) {
}
