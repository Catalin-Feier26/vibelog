// src/main/java/com/catalin/vibelog/dto/response/ReportResponseDTO.java
package com.catalin.vibelog.dto.response;

import com.catalin.vibelog.model.enums.ReportStatus;
import java.time.LocalDateTime;

/**
 * Response DTO representing a report record.
 *
 * @param id                 unique report ID
 * @param reporterUsername   username of the user who filed the report
 * @param postId             ID of the reported post, or null
 * @param commentId          ID of the reported comment, or null
 * @param reason             reporter’s explanation
 * @param status             current report status
 * @param reportedAt         timestamp when the report was created
 */
public record ReportResponseDTO(
        Long id,
        String reporterUsername,
        Long postId,
        Long commentId,
        String reason,
        ReportStatus status,
        LocalDateTime reportedAt
) { }
