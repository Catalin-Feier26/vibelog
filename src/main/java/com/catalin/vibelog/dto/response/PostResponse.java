package com.catalin.vibelog.dto.response;

import java.time.LocalDateTime;

/**
 * DTO representing the data returned for a blog post.
 * <p>
 * Exposes read-only fields derived from the Post entity and its relationships.
 * </p>
 *
 * @param id              unique identifier of the post
 * @param title           title of the post
 * @param body            main content of the post
 * @param status          current status of the post (e.g., DRAFT, PUBLISHED)
 * @param createdAt       timestamp when the post was created
 * @param authorUsername  username of the post's author
 * @param likeCount       total number of likes on the post
 * @param commentCount    total number of comments on the post
 */
public record PostResponse(
        Long id,
        String title,
        String body,
        String status,
        LocalDateTime createdAt,
        String authorUsername,
        int likeCount,
        int commentCount
) {}
