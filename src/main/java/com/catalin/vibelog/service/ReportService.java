package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.ReportRequestDTO;
import com.catalin.vibelog.dto.request.ReportStatusUpdateDTO;
import com.catalin.vibelog.dto.response.ReportResponseDTO;
import com.catalin.vibelog.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Business operations for managing content reports.
 */
public interface ReportService {

    /**
     * Submit a new report on a post or comment.
     *
     * @param reporterUsername username of the reporter
     * @param request          payload containing target and reason
     * @return the created report as a DTO
     */
    ReportResponseDTO submitReport(String reporterUsername, ReportRequestDTO request);

    /**
     * List all reports with a given status.
     *
     * @param status   filter by {@link ReportStatus}
     * @param pageable paging parameters
     * @return paged list of report DTOs
     */
    Page<ReportResponseDTO> listReportsByStatus(ReportStatus status, Pageable pageable);

    /**
     * List all reports filed by a specific user.
     *
     * @param reporterUsername the reporter’s username
     * @param pageable         paging parameters
     * @return paged list of report DTOs
     */
    Page<ReportResponseDTO> listReportsByReporter(String reporterUsername, Pageable pageable);

    /**
     * Update the status of an existing report.
     *
     * @param reportId     ID of the report to update
     * @param statusUpdate payload containing the new status
     * @return the updated report as a DTO
     */
    ReportResponseDTO updateReportStatus(Long reportId, ReportStatusUpdateDTO statusUpdate);
}
