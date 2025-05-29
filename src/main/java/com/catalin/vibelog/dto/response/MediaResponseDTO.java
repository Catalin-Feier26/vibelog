package com.catalin.vibelog.dto.response;

/**
 * Data Transfer Object representing media attached to a post.
 * <p>
 * Contains the media ID, publicly accessible URL, and media type (e.g., image or video).
 * </p>
 *
 * @param id   the unique identifier of the media
 * @param url  the publicly accessible URL or path to the media resource
 * @param type the media type, typically corresponding to {@code MediaType.name()}
 */
public record MediaResponseDTO(
        Long id,
        String url,
        String type
) {}
