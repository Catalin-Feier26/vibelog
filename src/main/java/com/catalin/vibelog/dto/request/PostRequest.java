package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a blog post.
 * <p>
 * Contains only the mutable fields of a Post entity. Status defaults to PUBLISHED if omitted.
 * </p>
 *
 * @param title  the title of the post; must not be blank and have max length 200
 * @param body   the main content of the post; must not be blank
 * @param status the status string (e.g., "DRAFT" or "PUBLISHED"); case-insensitive
 */
public record PostRequest(
        @NotBlank(message = "Title must not be blank")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Body must not be blank")
        String body,

        String status
) {}
