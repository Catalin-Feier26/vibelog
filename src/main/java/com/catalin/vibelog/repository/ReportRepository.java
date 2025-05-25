// src/main/java/com/catalin/vibelog/repository/ReportRepository.java
package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Report;
import com.catalin.vibelog.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Report} entities.
 * Supports querying by status, reporter, and target content.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Find all reports with the given status.
     *
     * @param status   the report status to filter by
     * @param pageable paging information
     * @return a page of reports
     */
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * Find all reports filed by a specific user.
     *
     * @param username reporter's username
     * @param pageable paging information
     * @return a page of reports
     */
    Page<Report> findByReporterUsername(String username, Pageable pageable);

    /**
     * Find all reports against a given post.
     *
     * @param postId   ID of the reported post
     * @param pageable paging information
     * @return a page of reports
     */
    Page<Report> findByPostId(Long postId, Pageable pageable);

    /**
     * Find all reports against a given comment.
     *
     * @param commentId ID of the reported comment
     * @param pageable  paging information
     * @return a page of reports
     */
    Page<Report> findByCommentId(Long commentId, Pageable pageable);
}
