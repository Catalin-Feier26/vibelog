package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.catalin.vibelog.events.FollowEvent;
import com.catalin.vibelog.model.Follow;
import com.catalin.vibelog.model.FollowId;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.FollowRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.implementations.FollowServiceImpl;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock private FollowRepository followRepo;
    @Mock private UserRepository userRepo;
    @Mock private ApplicationEventPublisher publisher;

    @InjectMocks
    private FollowServiceImpl followService;

    @Test
    void isFollowing_WhenFollowerNotFound_Throws() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.isFollowing(1L, 2L));
    }

    @Test
    void isFollowing_WhenFolloweeNotFound_Throws() {
        var follower = new RegularUser(); follower.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.isFollowing(1L, 2L));
    }

    @Test
    void isFollowing_ReturnsTrueOrFalse() {
        var follower = new RegularUser(); follower.setId(1L);
        var followee = new RegularUser(); followee.setId(2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepo.findById(2L)).thenReturn(Optional.of(followee));
        when(followRepo.findByFollowerAndFollowed(follower, followee))
                .thenReturn(Optional.of(new Follow()));
        assertTrue(followService.isFollowing(1L, 2L));
        when(followRepo.findByFollowerAndFollowed(follower, followee))
                .thenReturn(Optional.empty());
        assertFalse(followService.isFollowing(1L, 2L));
    }

    @Test
    void follow_PreventsSelfFollow() {
        // Should do nothing
        followService.follow(1L, 1L);
        verifyNoInteractions(userRepo, followRepo, publisher);
    }

    @Test
    void follow_WhenFollowerNotFound_Throws() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.follow(1L, 2L));
    }

    @Test
    void follow_WhenFolloweeNotFound_Throws() {
        var follower = new RegularUser(); follower.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.follow(1L, 2L));
    }

    @Test
    void follow_WhenAlreadyFollowing_DoesNothing() {
        var follower = new RegularUser(); follower.setId(1L);
        var followee = new RegularUser(); followee.setId(2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepo.findById(2L)).thenReturn(Optional.of(followee));
        when(followRepo.findByFollowerAndFollowed(follower, followee))
                .thenReturn(Optional.of(new Follow()));
        followService.follow(1L, 2L);
        verify(followRepo, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void unfollow_WhenFollowerNotFound_Throws() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.unfollow(1L, 2L));
    }

    @Test
    void unfollow_WhenFolloweeNotFound_Throws() {
        var follower = new RegularUser(); follower.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.unfollow(1L, 2L));
    }

    @Test
    void unfollow_WhenExists_Deletes() {
        var follower = new RegularUser(); follower.setId(1L);
        var followee = new RegularUser(); followee.setId(2L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepo.findById(2L)).thenReturn(Optional.of(followee));
        followService.unfollow(1L, 2L);
        verify(followRepo).deleteByFollowerAndFollowed(follower, followee);
    }

    @Test
    void countFollowers_DelegatesOrThrows() {
        var user = new RegularUser(); user.setId(3L);
        when(userRepo.findById(3L)).thenReturn(Optional.of(user));
        when(followRepo.countByFollowed(user)).thenReturn(5L);
        assertEquals(5L, followService.countFollowers(3L));
        when(userRepo.findById(4L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.countFollowers(4L));
    }

    @Test
    void countFollowing_DelegatesOrThrows() {
        var user = new RegularUser(); user.setId(6L);
        when(userRepo.findById(6L)).thenReturn(Optional.of(user));
        when(followRepo.countByFollower(user)).thenReturn(7L);
        assertEquals(7L, followService.countFollowing(6L));
        when(userRepo.findById(8L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> followService.countFollowing(8L));
    }
}