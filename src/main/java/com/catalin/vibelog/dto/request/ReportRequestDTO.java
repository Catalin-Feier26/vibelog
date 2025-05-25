// src/main/java/com/catalin/vibelog/dto/request/ReportRequestDTO.java
package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload for submitting a new content report.
 *
 * @param postId    ID of the post being reported; null if reporting a comment
 * @param commentId ID of the comment being reported; null if reporting a post
 * @param reason    explanation provided by the reporter
 */
public record ReportRequestDTO(
        Long postId,
        Long commentId,
        @NotBlank(message = "Reason must be provided")
        String reason
) { }
