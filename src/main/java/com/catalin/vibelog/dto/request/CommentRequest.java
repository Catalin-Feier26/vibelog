package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a comment on a post.
 * <p>
 * Only the content of the comment is mutable; the post and author are derived from context.
 * </p>
 *
 * @param content the text of the comment; must not be blank and have max length 1000
 */
public record CommentRequest(
        @NotBlank(message = "Comment content must not be blank")
        @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
        String content
) {}
