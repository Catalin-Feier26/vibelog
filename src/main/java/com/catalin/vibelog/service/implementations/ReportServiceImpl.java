package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.ReportRequestDTO;
import com.catalin.vibelog.dto.request.ReportStatusUpdateDTO;
import com.catalin.vibelog.dto.response.ReportResponseDTO;
import com.catalin.vibelog.events.ReportResolvedEvent;
import com.catalin.vibelog.exception.CommentNotFoundException;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.ReportNotFoundException;
import com.catalin.vibelog.exception.UserNotFoundException;
import com.catalin.vibelog.model.Comment;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.Report;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.ReportStatus;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.ReportRepository;
import com.catalin.vibelog.service.ReportService;
import com.catalin.vibelog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ReportService}, handling creation, retrieval,
 * and status updates for reports against posts or comments.
 * Publishes a {@link ReportResolvedEvent} when a report is resolved.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ApplicationEventPublisher publisher;
    private final ReportRepository reportRepo;
    private final UserService userService;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;

    /**
     * Constructs the service with required dependencies.
     *
     * @param publisher    event publisher for firing domain events
     * @param reportRepo   repository for report persistence
     * @param userService  service for validating and retrieving users
     * @param postRepo     repository for post lookups
     * @param commentRepo  repository for comment lookups
     */
    @Autowired
    public ReportServiceImpl(
            ApplicationEventPublisher publisher,
            ReportRepository reportRepo,
            UserService userService,
            PostRepository postRepo,
            CommentRepository commentRepo
    ) {
        this.publisher = publisher;
        this.reportRepo = reportRepo;
        this.userService = userService;
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
    }

    /**
     * Submit a new report against either a post or a comment.
     * Exactly one of {@code postId} or {@code commentId} must be non-null.
     *
     * @param reporterUsername the username of the reporting user
     * @param req              details of the report, including target and reason
     * @return a {@link ReportResponseDTO} representing the persisted report
     * @throws IllegalArgumentException   if both or neither of postId/commentId are provided
     * @throws UserNotFoundException      if the reporting user does not exist
     * @throws PostNotFoundException      if the specified post does not exist
     * @throws CommentNotFoundException   if the specified comment does not exist
     */
    @Override
    public ReportResponseDTO submitReport(String reporterUsername, ReportRequestDTO req) {
        // lookup reporter
        User reporter = userService.findByUsername(reporterUsername);

        // must reference exactly one target
        boolean hasPost = req.postId() != null;
        boolean hasComment = req.commentId() != null;
        if (hasPost == hasComment) {
            throw new IllegalArgumentException(
                    "Report must reference exactly one of postId or commentId"
            );
        }

        Report.Builder b = Report.builder()
                .reporter(reporter)
                .reason(req.reason());

        if (hasPost) {
            Post post = postRepo.findById(req.postId())
                    .orElseThrow(() -> new PostNotFoundException(req.postId()));
            b.post(post);
        } else {
            Comment comment = commentRepo.findById(req.commentId())
                    .orElseThrow(() -> new CommentNotFoundException(req.commentId()));
            b.comment(comment);
        }

        Report saved = reportRepo.save(b.build());
        return toDto(saved);
    }

    /**
     * List reports filtered by their status.
     *
     * @param status the status of reports to retrieve
     * @param page   pagination and sorting information
     * @return a {@link Page} of {@link ReportResponseDTO} matching the status
     */
    @Override
    public Page<ReportResponseDTO> listReportsByStatus(ReportStatus status, Pageable page) {
        return reportRepo.findByStatus(status, page)
                .map(this::toDto);
    }

    /**
     * List reports submitted by a specific user.
     *
     * @param reporterUsername the username of the reporting user
     * @param page             pagination and sorting information
     * @return a {@link Page} of {@link ReportResponseDTO} for that user
     * @throws UserNotFoundException if the reporting user does not exist
     */
    @Override
    public Page<ReportResponseDTO> listReportsByReporter(String reporterUsername, Pageable page) {
        // validate reporter exists
        userService.findByUsername(reporterUsername);
        return reportRepo.findByReporterUsername(reporterUsername, page)
                .map(this::toDto);
    }

    /**
     * Update the status of an existing report and publish a resolved event if applicable.
     *
     * @param reportId the ID of the report to update
     * @param upd      DTO containing the new status
     * @return a {@link ReportResponseDTO} reflecting the updated report
     * @throws ReportNotFoundException if no report exists with the given ID
     */
    @Override
    public ReportResponseDTO updateReportStatus(Long reportId, ReportStatusUpdateDTO upd) {
        Report rep = reportRepo.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        rep.setStatus(upd.status());
        Report saved = reportRepo.save(rep);
        if (saved.getStatus().equals(ReportStatus.RESOLVED)) {
            publisher.publishEvent(new ReportResolvedEvent(
                    this,
                    saved.getId(),
                    saved.getReporter().getUsername(),
                    saved.getPost() != null ? saved.getPost().getId() : null,
                    saved.getComment() != null ? saved.getComment().getId() : null,
                    saved.getStatus()
            ));
        }
        return toDto(saved);
    }

    /**
     * Map a {@link Report} entity to a {@link ReportResponseDTO}.
     *
     * @param r the report entity to convert
     * @return the corresponding response DTO
     */
    private ReportResponseDTO toDto(Report r) {
        return new ReportResponseDTO(
                r.getId(),
                r.getReporter().getUsername(),
                r.getPost()    != null ? r.getPost().getId()    : null,
                r.getComment() != null ? r.getComment().getId() : null,
                r.getReason(),
                r.getStatus(),
                r.getReportedAt()
        );
    }
}
