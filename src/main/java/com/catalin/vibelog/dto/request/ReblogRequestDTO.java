package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for creating or deleting a reblog.
 *
 * @param rebloggerUsername the user who is (un)reblogging
 * @param originalPostId    the ID of the post being reblogged
 */
public record ReblogRequestDTO(
        @NotBlank
        String rebloggerUsername,

        @NotNull
        Long originalPostId
) { }