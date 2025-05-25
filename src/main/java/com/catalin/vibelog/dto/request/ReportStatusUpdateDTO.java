package com.catalin.vibelog.dto.request;

import com.catalin.vibelog.model.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Payload for updating the status of a report.
 *
 * @param status new status for the report
 */
public record ReportStatusUpdateDTO(
        @NotNull(message = "Status must be provided")
        ReportStatus status
) { }
