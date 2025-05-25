package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.ReportRequestDTO;
import com.catalin.vibelog.dto.request.ReportStatusUpdateDTO;
import com.catalin.vibelog.dto.response.ReportResponseDTO;
import com.catalin.vibelog.model.enums.ReportStatus;
import com.catalin.vibelog.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for submitting and reviewing content reports.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Submit a new report. Authenticated users may report posts or comments.
     *
     * @param auth    the current Authentication (provides username)
     * @param request the report payload
     * @return the created report
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponseDTO submitReport(
            Authentication auth,
            @Valid @RequestBody ReportRequestDTO request
    ) {
        String reporter = auth.getName();
        return reportService.submitReport(reporter, request);
    }

    /**
     * List all reports by status (for moderators).
     *
     * @param status   filter by report status
     * @param pageable paging parameters
     * @return a page of reports
     */
    @GetMapping
    @PreAuthorize("hasRole('MODERATOR')")
    public Page<ReportResponseDTO> listReportsByStatus(
            @RequestParam(defaultValue = "PENDING") ReportStatus status,
            Pageable pageable
    ) {
        return reportService.listReportsByStatus(status, pageable);
    }

    /**
     * List the current user's own reports.
     *
     * @param auth     the current Authentication
     * @param pageable paging parameters
     * @return a page of reports this user filed
     */
    @GetMapping("/my")
    public Page<ReportResponseDTO> listMyReports(
            Authentication auth,
            Pageable pageable
    ) {
        return reportService.listReportsByReporter(auth.getName(), pageable);
    }

    /**
     * Change the status of an existing report (moderator only).
     *
     * @param reportId     the ID of the report to update
     * @param statusUpdate the new status payload
     * @return the updated report
     */
    @PutMapping("/{reportId}/status")
    @PreAuthorize("hasRole('MODERATOR')")
    public ReportResponseDTO updateReportStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody ReportStatusUpdateDTO statusUpdate
    ) {
        return reportService.updateReportStatus(reportId, statusUpdate);
    }
}
