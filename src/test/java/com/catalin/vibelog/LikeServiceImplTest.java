package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.catalin.vibelog.dto.response.LikeResponse;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.model.Like;
import com.catalin.vibelog.model.LikeId;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.implementations.LikeServiceImpl;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock private LikeRepository likeRepo;
    @Mock private PostRepository postRepo;
    @Mock private UserRepository userRepo;
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    void toggleLike_WhenPostNotFound_Throws() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> likeService.toggleLike(1L, "alice"));
    }

    @Test
    void toggleLike_WhenUserNotFound_Throws() {
        var post = new Post(); post.setId(2L);
        when(postRepo.findById(2L)).thenReturn(Optional.of(post));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> likeService.toggleLike(2L, "bob"));
    }

    @Test
    void toggleLike_WhenUserIsAuthor_DoesNotPublishEvent() {
        var post = new Post(); post.setId(11L);
        var author = new RegularUser(); author.setId(12L); author.setUsername("xander");
        post.setAuthor(author);
        when(postRepo.findById(11L)).thenReturn(Optional.of(post));
        when(userRepo.findByUsername("xander")).thenReturn(Optional.of(author));
        LikeId id = new LikeId(12L, 11L);
        when(likeRepo.existsById(id)).thenReturn(false);
        when(likeRepo.countByIdPostId(11L)).thenReturn(1);
        when(likeRepo.save(any(Like.class))).thenAnswer(inv -> inv.getArgument(0));

        LikeResponse resp = likeService.toggleLike(11L, "xander");

        assertTrue(resp.liked());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void countLikes_DelegatesToRepository() {
        when(likeRepo.countByIdPostId(13L)).thenReturn(8);
        assertEquals(8, likeService.countLikes(13L));
        verify(likeRepo).countByIdPostId(13L);
    }

    @Test
    void isLiked_WhenUserNotFound_Throws() {
        when(userRepo.findByUsername("nope")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> likeService.isLiked(14L, "nope"));
    }

    @Test
    void isLiked_ReturnsExists() {
        var user = new RegularUser(); user.setId(15L);
        when(userRepo.findByUsername("pat")).thenReturn(Optional.of(user));
        LikeId id = new LikeId(15L, 16L);
        when(likeRepo.existsById(id)).thenReturn(true);
        assertTrue(likeService.isLiked(16L, "pat"));
        verify(likeRepo).existsById(id);
    }
}
