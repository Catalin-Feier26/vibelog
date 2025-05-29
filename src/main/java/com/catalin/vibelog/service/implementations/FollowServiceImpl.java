package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.events.FollowEvent;
import com.catalin.vibelog.model.Follow;
import com.catalin.vibelog.model.FollowId;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.FollowRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.FollowService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link FollowService}, providing operations
 * for following and unfollowing users, checking follow status, and counting followers/following.
 * <p>
 * All methods execute within a transaction to maintain consistency.
 * </p>
 */
@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final ApplicationEventPublisher publisher;
    private final FollowRepository followRepo;
    private final UserRepository userRepo;

    /**
     * Constructs the FollowServiceImpl with required dependencies.
     *
     * @param publisher  event publisher for firing follow/unfollow notifications
     * @param followRepo repository for persisting and querying follow relationships
     * @param userRepo   repository for retrieving user entities
     */
    public FollowServiceImpl(ApplicationEventPublisher publisher,
                             FollowRepository followRepo,
                             UserRepository userRepo) {
        this.publisher = publisher;
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    /**
     * Check if a user is following another user.
     *
     * @param followerId the ID of the potential follower
     * @param followeeId the ID of the potential followee
     * @return {@code true} if the follower is following the followee, {@code false} otherwise
     * @throws EntityNotFoundException if either user cannot be found
     */
    @Override
    public boolean isFollowing(Long followerId, Long followeeId) {
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower not found: " + followerId));
        User followee = userRepo.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("Followee not found: " + followeeId));
        return followRepo.findByFollowerAndFollowed(follower, followee).isPresent();
    }

    /**
     * Create a follow relationship from one user to another.
     * Prevents self-follow and duplicate follows.
     * Publishes a {@link FollowEvent} upon successful follow.
     *
     * @param followerId the ID of the follower
     * @param followeeId the ID of the user to be followed
     * @throws EntityNotFoundException if either user cannot be found
     */
    @Override
    public void follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            // Prevent self-follow
            return;
        }
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("User (follower) not found: " + followerId));
        User followee = userRepo.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("User (followee) not found: " + followeeId));

        if (followRepo.findByFollowerAndFollowed(follower, followee).isEmpty()) {
            Follow follow = new Follow();
            follow.setId(new FollowId(followerId, followeeId));
            follow.setFollower(follower);
            follow.setFollowed(followee);
            followRepo.save(follow);
            publisher.publishEvent(new FollowEvent(
                    this,
                    follower.getUsername(),
                    followee.getUsername()
            ));
        }
    }

    /**
     * Remove an existing follow relationship.
     *
     * @param followerId the ID of the follower
     * @param followeeId the ID of the followee to unfollow
     * @throws EntityNotFoundException if either user cannot be found
     */
    @Override
    public void unfollow(Long followerId, Long followeeId) {
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("User (follower) not found: " + followerId));
        User followee = userRepo.findById(followeeId)
                .orElseThrow(() -> new EntityNotFoundException("User (followee) not found: " + followeeId));
        followRepo.deleteByFollowerAndFollowed(follower, followee);
    }

    /**
     * Count how many followers a user has.
     *
     * @param userId the ID of the user whose followers to count
     * @return the number of followers
     * @throws EntityNotFoundException if the user cannot be found
     */
    @Override
    public long countFollowers(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        return followRepo.countByFollowed(user);
    }

    /**
     * Count how many users a given user is following.
     *
     * @param userId the ID of the user whose followees to count
     * @return the number of users being followed
     * @throws EntityNotFoundException if the user cannot be found
     */
    @Override
    public long countFollowing(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        return followRepo.countByFollower(user);
    }
}