// src/main/java/com/catalin/vibelog/dto/response/PostResponse.java
package com.catalin.vibelog.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for a blog post, including reblog metadata if present.
 *
 * @param id                       unique identifier
 * @param title                    post title
 * @param body                     post content
 * @param status                   status (e.g. DRAFT, PUBLISHED)
 * @param createdAt                creation timestamp
 * @param authorUsername           original author’s username
 * @param likeCount                total likes
 * @param commentCount             total comments
 * @param originalPostId           ID of the post this reblog points to, or null
 * @param originalAuthorUsername   username of the original post’s author, or null
 * @param media                    list of media attachments
 */
public record PostResponse(
        Long   id,
        String title,
        String body,
        String status,
        LocalDateTime createdAt,
        String authorUsername,
        int    likeCount,
        int    commentCount,
        Long   originalPostId,
        String originalAuthorUsername,
        List<MediaResponseDTO> media
) {}
