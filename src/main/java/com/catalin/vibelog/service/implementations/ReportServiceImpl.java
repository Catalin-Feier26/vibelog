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
 * Default {@link ReportService} implementation,
 * handling persistence and business rules for reports.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ApplicationEventPublisher publisher;
    private final ReportRepository reportRepo;
    private final UserService userService;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;

    @Autowired
    public ReportServiceImpl(
            ApplicationEventPublisher publisher,
            ReportRepository reportRepo,
            UserService userService,
            PostRepository postRepo,
            CommentRepository commentRepo
    ) {
        this.publisher=publisher;
        this.reportRepo    = reportRepo;
        this.userService   = userService;
        this.postRepo      = postRepo;
        this.commentRepo   = commentRepo;
    }

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

    @Override
    public Page<ReportResponseDTO> listReportsByStatus(ReportStatus status, Pageable page) {
        return reportRepo.findByStatus(status, page)
                .map(this::toDto);
    }

    @Override
    public Page<ReportResponseDTO> listReportsByReporter(String reporterUsername, Pageable page) {
        // validate reporter exists
        userService.findByUsername(reporterUsername);
        return reportRepo.findByReporterUsername(reporterUsername, page)
                .map(this::toDto);
    }

    @Override
    public ReportResponseDTO updateReportStatus(Long reportId, ReportStatusUpdateDTO upd) {
        Report rep = reportRepo.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        rep.setStatus(upd.status());
        Report saved = reportRepo.save(rep);
        if(saved.getStatus().equals(ReportStatus.RESOLVED)) {
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
