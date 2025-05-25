// src/main/java/com/catalin/vibelog/dto/request/PostRequest.java
package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a blog post.
 * <p>
 * Contains mutable fields of a Post entity.
 * If {@code originalPostId} is non-null, the new post is treated as a reblog.
 * </p>
 *
 * @param title           the title; must not be blank, max 200 chars
 * @param body            the main content; must not be blank
 * @param status          the status string (e.g. "DRAFT" or "PUBLISHED")
 * @param originalPostId  optional ID of the post being reblogged
 */
public record PostRequest(
        @NotBlank(message = "Title must not be blank")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @NotBlank(message = "Body must not be blank")
        String body,

        String status,

        Long originalPostId
) {}
