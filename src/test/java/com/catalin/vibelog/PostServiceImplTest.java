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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.MediaResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.events.ReblogEvent;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UnauthorizedActionException;
import com.catalin.vibelog.exception.UserNotFoundException;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.PostStatus;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.ReportRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.MediaService;
import com.catalin.vibelog.service.implementations.PostServiceImpl;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock private ReportRepository reportRepo;
    @Mock private PostRepository postRepo;
    @Mock private UserRepository userRepo;
    @Mock private CommentRepository commentRepo;
    @Mock private LikeRepository likeRepo;
    @Mock private ApplicationEventPublisher publisher;
    @Mock private MediaService mediaService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void createPost_WhenNoOriginal_CreatesAndReturnsDto() {
        var req = new PostRequest("T", "B", "PUBLISHED", null);
        var author = new RegularUser(); author.setUsername("alice");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(author));

        // saved entity stub
        Post saved = new Post();
        saved.setId(10L);
        saved.setTitle("T"); saved.setBody("B");
        saved.setStatus(PostStatus.PUBLISHED);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setAuthor(author);
        when(postRepo.save(any(Post.class))).thenReturn(saved);

        PostResponse dto = postService.createPost(req, "alice");

        assertEquals(10L, dto.id());
        assertEquals("T", dto.title());
        assertEquals("B", dto.body());
        assertEquals("PUBLISHED", dto.status());
        assertEquals("alice", dto.authorUsername());
        assertEquals(0, dto.likeCount());
        assertEquals(0, dto.commentCount());
        assertNull(dto.originalPostId());
        assertNull(dto.originalAuthorUsername());
        assertTrue(dto.media().isEmpty());

        verify(userRepo).findByUsername("alice");
        verify(postRepo).save(any(Post.class));
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void getPostById_WhenNotFound_Throws() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
    }

    @Test
    void getPostById_WhenFound_ReturnsDto() {
        var p = new Post(); p.setId(2L);
        p.setTitle("X"); p.setBody("Y");
        p.setStatus(PostStatus.PUBLISHED);
        p.setCreatedAt(LocalDateTime.of(2020,1,1,0,0));
        var author = new RegularUser(); author.setUsername("u");
        p.setAuthor(author);
        when(postRepo.findById(2L)).thenReturn(Optional.of(p));

        PostResponse dto = postService.getPostById(2L);
        assertEquals(2L, dto.id());
        assertEquals("X", dto.title());
        assertEquals("Y", dto.body());
        assertEquals("PUBLISHED", dto.status());
        assertEquals("u", dto.authorUsername());
    }

    @Test
    void updatePost_WhenNotAuthor_Throws() {
        Post p = new Post(); p.setId(3L);
        var author = new RegularUser(); author.setUsername("a");
        p.setAuthor(author);
        when(postRepo.findById(3L)).thenReturn(Optional.of(p));

        var req = new PostRequest("T","B","DRAFT",null);
        assertThrows(UnauthorizedActionException.class,
                () -> postService.updatePost(3L, req, "other"));
    }

    @Test
    void updatePost_WhenAuthorized_SavesAndReturnsDto() {
        Post p = new Post(); p.setId(4L);
        var author = new RegularUser(); author.setUsername("me");
        p.setAuthor(author);
        when(postRepo.findById(4L)).thenReturn(Optional.of(p));

        var req = new PostRequest("New","Body","PUBLISHED",null);
        p.setTitle("New"); p.setBody("Body");
        p.setStatus(PostStatus.PUBLISHED);
        p.setUpdatedAt(LocalDateTime.now());
        when(postRepo.save(p)).thenReturn(p);

        PostResponse dto = postService.updatePost(4L, req, "me");
        assertEquals("New", dto.title());
        assertEquals("Body", dto.body());
        assertEquals("PUBLISHED", dto.status());
    }

    @Test
    void deletePost_WhenAuthorized_Deletes() {
        Post p = new Post(); p.setId(5L);
        var author = new RegularUser(); author.setUsername("me");
        p.setAuthor(author);
        when(postRepo.findById(5L)).thenReturn(Optional.of(p));

        postService.deletePost(5L, "me");
        verify(postRepo).delete(p);
    }

    @Test
    void deletePost_WhenNotAuthor_Throws() {
        Post p = new Post(); p.setId(6L);
        var author = new RegularUser(); author.setUsername("them");
        p.setAuthor(author);
        when(postRepo.findById(6L)).thenReturn(Optional.of(p));

        assertThrows(UnauthorizedActionException.class,
                () -> postService.deletePost(6L, "you"));
    }

    @Test
    void undoReblog_WhenExists_Deletes() {
        var reblog = new Post(); reblog.setId(8L);
        when(postRepo.findByAuthorUsernameAndOriginalPostId("u", 9L))
                .thenReturn(Optional.of(reblog));

        postService.undoReblog("u", 9L);
        verify(postRepo).delete(reblog);
    }

    @Test
    void undoReblog_WhenNotExists_Throws() {
        when(postRepo.findByAuthorUsernameAndOriginalPostId("u", 9L))
                .thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,
                () -> postService.undoReblog("u", 9L));
    }

    @Test
    void isReblogged_ReturnsExists() {
        when(postRepo.existsByAuthorUsernameAndOriginalPostId("u",9L))
                .thenReturn(true);
        assertTrue(postService.isReblogged("u",9L));
    }

    @Test
    void countReblogs_ReturnsCount() {
        when(postRepo.countByOriginalPostId(10L)).thenReturn(5);
        assertEquals(5, postService.countReblogs(10L));
    }

    @Test
    void deletePostAsModerator_CleansReportsAndDeletes() {
        var p = new Post(); p.setId(11L);
        when(postRepo.findById(11L)).thenReturn(Optional.of(p));

        postService.deletePostAsModerator(11L);
        verify(reportRepo).deleteAllByPostId(11L);
        verify(postRepo).delete(p);
    }
}
