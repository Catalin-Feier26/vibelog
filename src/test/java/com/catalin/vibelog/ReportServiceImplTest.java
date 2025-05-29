package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import com.catalin.vibelog.dto.request.ReportRequestDTO;
import com.catalin.vibelog.dto.request.ReportStatusUpdateDTO;
import com.catalin.vibelog.exception.CommentNotFoundException;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.ReportNotFoundException;
import com.catalin.vibelog.exception.UserNotFoundException;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.Report;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.enums.ReportStatus;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.ReportRepository;
import com.catalin.vibelog.service.UserService;
import com.catalin.vibelog.service.implementations.ReportServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private ReportRepository reportRepo;
    @Mock
    private UserService userService;
    @Mock
    private PostRepository postRepo;
    @Mock
    private CommentRepository commentRepo;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void submitReport_WhenNeitherPostNorComment_ThrowsIllegalArgument() {
        var req = new ReportRequestDTO(null, null, "reason");
        assertThrows(IllegalArgumentException.class,
                () -> reportService.submitReport("user1", req));
    }

    @Test
    void submitReport_WhenBothPostAndComment_ThrowsIllegalArgument() {
        var req = new ReportRequestDTO(1L, 2L, "reason");
        assertThrows(IllegalArgumentException.class,
                () -> reportService.submitReport("user1", req));
    }

    @Test
    void submitReport_WhenReporterNotFound_ThrowsUserNotFoundException() {
        when(userService.findByUsername("user1")).thenThrow(new UserNotFoundException("user1"));
        var req = new ReportRequestDTO(1L, null, "reason");
        assertThrows(UserNotFoundException.class,
                () -> reportService.submitReport("user1", req));
        verify(userService).findByUsername("user1");
    }

    @Test
    void submitReport_WhenPostNotFound_ThrowsPostNotFoundException() {
        var reporter = new RegularUser(); reporter.setUsername("user1");
        when(userService.findByUsername("user1")).thenReturn(reporter);
        when(postRepo.findById(1L)).thenReturn(Optional.empty());

        var req = new ReportRequestDTO(1L, null, "reason");
        assertThrows(PostNotFoundException.class,
                () -> reportService.submitReport("user1", req));
        verify(postRepo).findById(1L);
    }

    @Test
    void submitReport_WhenCommentNotFound_ThrowsCommentNotFoundException() {
        var reporter = new RegularUser(); reporter.setUsername("user1");
        when(userService.findByUsername("user1")).thenReturn(reporter);
        when(commentRepo.findById(2L)).thenReturn(Optional.empty());

        var req = new ReportRequestDTO(null, 2L, "reason");
        assertThrows(CommentNotFoundException.class,
                () -> reportService.submitReport("user1", req));
        verify(commentRepo).findById(2L);
    }

    @Test
    void submitReport_WhenValidPost_ReturnsReportResponse() {
        var reporter = new RegularUser(); reporter.setUsername("user1");
        when(userService.findByUsername("user1")).thenReturn(reporter);
        var post = new Post(); post.setId(1L);
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));

        var req = new ReportRequestDTO(1L, null, "reason");
        var saved = Report.builder()
                .reporter(reporter)
                .post(post)
                .reason("reason")
                .build();
        saved.setId(100L);
        saved.setStatus(ReportStatus.PENDING);
        saved.setReportedAt(LocalDateTime.of(1970,1,1,0,0));
        when(reportRepo.save(any(Report.class))).thenReturn(saved);

        var res = reportService.submitReport("user1", req);

        assertEquals(100L,          res.id());
        assertEquals("user1",      res.reporterUsername());
        assertEquals(1L,            res.postId());
        assertNull(                  res.commentId());
        assertEquals("reason",     res.reason());
        assertEquals(ReportStatus.PENDING, res.status());
        assertEquals(LocalDateTime.of(1970,1,1,0,0), res.reportedAt());
        verify(reportRepo).save(any(Report.class));
    }

    @Test
    void listReportsByReporter_WhenUserNotFound_ThrowsUserNotFoundException() {
        when(userService.findByUsername("user1")).thenThrow(new UserNotFoundException("user1"));
        assertThrows(UserNotFoundException.class,
                () -> reportService.listReportsByReporter("user1", PageRequest.of(0, 1)));
    }

    @Test
    void updateReportStatus_WhenNotFound_ThrowsReportNotFoundException() {
        when(reportRepo.findById(10L)).thenReturn(Optional.empty());
        var upd = new ReportStatusUpdateDTO(ReportStatus.RESOLVED);
        assertThrows(ReportNotFoundException.class,
                () -> reportService.updateReportStatus(10L, upd));
    }

}