package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.model.Follow;
import com.catalin.vibelog.model.FollowId;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.FollowRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.FollowService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link FollowService},
 * using JPA repositories for persistence.
 */
@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepo;
    private final UserRepository userRepo;

    @Autowired
    public FollowServiceImpl(FollowRepository followRepo, UserRepository userRepo) {
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFollowing(Long followerId, Long followeeId) {
        return followRepo
                .findByFollowerAndFollowed(
                        userRepo.findById(followerId)
                                .orElseThrow(() -> new EntityNotFoundException("Follower not found")),
                        userRepo.findById(followeeId)
                                .orElseThrow(() -> new EntityNotFoundException("Followee not found")))
                .isPresent();
    }

    /** {@inheritDoc} */
    @Override
    public void follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            // Prevent self‐follow
            return;
        }
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("User (follower) not found"));
        User followee = userRepo.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("User (followee) not found"));

        if (!followRepo.findByFollowerAndFollowed(follower, followee).isPresent()) {
            Follow f = new Follow();
            f.setId(new FollowId(followerId, followeeId));
            f.setFollower(follower);
            f.setFollowed(followee);
            followRepo.save(f);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unfollow(Long followerId, Long followeeId) {
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("User (follower) not found"));
        User followee = userRepo.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("User (followee) not found"));
        followRepo.deleteByFollowerAndFollowed(follower, followee);
    }

    /** {@inheritDoc} */
    @Override
    public long countFollowers(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return followRepo.countByFollowed(u);
    }

    /** {@inheritDoc} */
    @Override
    public long countFollowing(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return followRepo.countByFollower(u);
    }
}