package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Report;
import com.catalin.vibelog.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for managing {@link Report} entities.
 * <p>
 * Provides methods to query reports by status, reporter username,
 * and target content (post or comment), as well as delete bulk reports
 * by target identifiers.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Retrieve a paginated list of reports filtered by their status.
     *
     * @param status   the {@link ReportStatus} to filter by
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Report} entities matching the status
     */
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    /**
     * Retrieve a paginated list of reports submitted by a specific user.
     *
     * @param username the username of the reporter
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Report} entities filed by the reporter
     */
    Page<Report> findByReporterUsername(String username, Pageable pageable);

    /**
     * Retrieve a paginated list of reports targeting a specific post.
     *
     * @param postId   the ID of the reported post
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Report} entities for the post
     */
    Page<Report> findByPostId(Long postId, Pageable pageable);

    /**
     * Retrieve a paginated list of reports targeting a specific comment.
     *
     * @param commentId the ID of the reported comment
     * @param pageable  pagination and sorting information
     * @return a {@link Page} of {@link Report} entities for the comment
     */
    Page<Report> findByCommentId(Long commentId, Pageable pageable);

    /**
     * Delete all reports associated with the given post ID.
     *
     * @param postId the ID of the post whose reports should be removed
     */
    void deleteAllByPostId(Long postId);

    /**
     * Delete all reports associated with the given comment ID.
     *
     * @param commentId the ID of the comment whose reports should be removed
     */
    void deleteAllByCommentId(Long commentId);
}