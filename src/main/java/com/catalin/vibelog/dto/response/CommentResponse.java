package com.catalin.vibelog.dto.response;

import java.time.LocalDateTime;

/**
 * DTO representing a comment returned by the API.
 * <p>
 * Exposes read-only fields of a Comment entity and its relation to a Post.
 * </p>
 *
 * @param id              unique identifier of the comment
 * @param content         text content of the comment
 * @param authorUsername  username of the comment's author
 * @param createdAt       timestamp when the comment was created
 */
public record CommentResponse(
        Long id,
        String content,
        String authorUsername,
        LocalDateTime createdAt
) {}
