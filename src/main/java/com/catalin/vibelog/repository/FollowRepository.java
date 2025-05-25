package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Follow;
import com.catalin.vibelog.model.FollowId;
import com.catalin.vibelog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Follow entities, which represent
 * “user A follows user B” relationships.
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    /**
     * Looks up a follow relationship by the follower and the followed.
     *
     * @param follower the user who is doing the following
     * @param followed the user who is being followed
     * @return an Optional containing the Follow if it exists, or empty otherwise
     */
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    /**
     * Deletes the follow relationship between two users.
     *
     * @param follower the user who is doing the unfollowing
     * @param followed the user who is being unfollowed
     */
    void deleteByFollowerAndFollowed(User follower, User followed);

    /**
     * Counts how many users follow the given user.
     *
     * @param followed the user for whom we count followers
     * @return the number of followers
     */
    long countByFollowed(User followed);

    /**
     * Counts how many users the given user is following.
     *
     * @param follower the user for whom we count followings
     * @return the number of users being followed
     */
    long countByFollower(User follower);
}