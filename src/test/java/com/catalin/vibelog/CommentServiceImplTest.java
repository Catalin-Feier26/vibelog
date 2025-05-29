package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.catalin.vibelog.dto.request.CommentRequest;
import com.catalin.vibelog.dto.response.CommentResponse;
import com.catalin.vibelog.events.CommentEvent;
import com.catalin.vibelog.exception.CommentNotFoundException;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UnauthorizedActionException;
import com.catalin.vibelog.model.Comment;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.ReportRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.implementations.CommentServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock private ReportRepository reportRepo;
    @Mock private org.springframework.context.ApplicationEventPublisher publisher;
    @Mock private CommentRepository commentRepo;
    @Mock private PostRepository postRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void addComment_WhenPostNotFound_Throws() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());
        var req = new CommentRequest("Hi");
        assertThrows(PostNotFoundException.class,
                () -> commentService.addComment(1L, req, "alice"));
    }

    @Test
    void addComment_WhenUserNotFound_Throws() {
        var post = new Post(); post.setId(2L);
        when(postRepo.findById(2L)).thenReturn(Optional.of(post));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.empty());
        var req = new CommentRequest("Hello");
        assertThrows(IllegalStateException.class,
                () -> commentService.addComment(2L, req, "bob"));
    }

    @Test
    void listComments_WhenPostNotExists_Throws() {
        when(postRepo.existsById(4L)).thenReturn(false);
        assertThrows(PostNotFoundException.class,
                () -> commentService.listComments(4L));
    }

    @Test
    void listComments_WhenExists_ReturnsDtos() {
        long postId = 5L;
        when(postRepo.existsById(postId)).thenReturn(true);
        var c1 = new Comment(); c1.setId(20L); c1.setContent("C1");
        var u1 = new RegularUser(); u1.setUsername("eve"); c1.setAuthor(u1);
        c1.setCreatedAt(LocalDateTime.of(2022,2,2,2,2));
        when(commentRepo.findAllByPostId(postId, Sort.by("createdAt").ascending()))
                .thenReturn(List.of(c1));

        var list = commentService.listComments(postId);
        assertEquals(1, list.size());
        var dto = list.get(0);
        assertEquals(20L, dto.id());
        assertEquals("C1", dto.content());
        assertEquals("eve", dto.authorUsername());
        assertEquals(c1.getCreatedAt(), dto.createdAt());
    }

    @Test
    void updateComment_WhenNotFound_Throws() {
        when(commentRepo.findById(6L)).thenReturn(Optional.empty());
        var req = new CommentRequest("X");
        assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment(6L, req, "frank"));
    }

    @Test
    void updateComment_WhenNotAuthor_Throws() {
        var c = new Comment(); c.setId(7L);
        var auth = new RegularUser(); auth.setUsername("gina"); c.setAuthor(auth);
        when(commentRepo.findById(7L)).thenReturn(Optional.of(c));
        var req = new CommentRequest("Updated");
        assertThrows(UnauthorizedActionException.class,
                () -> commentService.updateComment(7L, req, "harry"));
    }

    @Test
    void updateComment_WhenAuthorized_SavesAndReturnsDto() {
        var c = new Comment(); c.setId(8L);
        var auth = new RegularUser(); auth.setUsername("ivan"); c.setAuthor(auth);
        c.setContent("Old"); c.setCreatedAt(LocalDateTime.now());
        when(commentRepo.findById(8L)).thenReturn(Optional.of(c));
        var req = new CommentRequest("NewContent");
        when(commentRepo.save(c)).thenAnswer(inv -> c);

        var res = commentService.updateComment(8L, req, "ivan");
        assertEquals(8L, res.id());
        assertEquals("NewContent", res.content());
    }

    @Test
    void deleteComment_WhenNotFound_Throws() {
        when(commentRepo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteComment(9L, "jane"));
    }

    @Test
    void deleteComment_WhenNotAuthor_Throws() {
        var c = new Comment(); c.setId(10L);
        var auth = new RegularUser(); auth.setUsername("kate"); c.setAuthor(auth);
        when(commentRepo.findById(10L)).thenReturn(Optional.of(c));
        assertThrows(UnauthorizedActionException.class,
                () -> commentService.deleteComment(10L, "leo"));
    }

    @Test
    void deleteComment_WhenAuthorized_Deletes() {
        var c = new Comment(); c.setId(11L);
        var auth = new RegularUser(); auth.setUsername("mia"); c.setAuthor(auth);
        when(commentRepo.findById(11L)).thenReturn(Optional.of(c));

        commentService.deleteComment(11L, "mia");
        verify(commentRepo).delete(c);
    }

    @Test
    void deleteCommentAsModerator_WhenNotFound_Throws() {
        when(commentRepo.findById(12L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteCommentAsModerator(12L));
    }

    @Test
    void deleteCommentAsModerator_WhenFound_DeletesReportsAndComment() {
        var c = new Comment(); c.setId(13L);
        when(commentRepo.findById(13L)).thenReturn(Optional.of(c));

        commentService.deleteCommentAsModerator(13L);
        verify(reportRepo).deleteAllByCommentId(13L);
        verify(commentRepo).delete(c);
    }
}
