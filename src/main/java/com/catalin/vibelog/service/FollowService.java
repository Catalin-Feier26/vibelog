package com.catalin.vibelog.service;

/**
 * Service interface defining follow/unfollow operations
 * and follower/following counts.
 */
public interface FollowService {

    /**
     * Checks whether one user is following another.
     *
     * @param followerId ID of the user who might be following
     * @param followeeId ID of the user who might be followed
     * @return true if followerId follows followeeId
     */
    boolean isFollowing(Long followerId, Long followeeId);

    /**
     * Creates a follow relationship between two users.
     * No‐op if they already follow or if IDs are equal.
     *
     * @param followerId ID of the user who will follow
     * @param followeeId ID of the user to be followed
     */
    void follow(Long followerId, Long followeeId);

    /**
     * Removes the follow relationship between two users.
     * No‐op if no such relationship exists.
     *
     * @param followerId ID of the user who will unfollow
     * @param followeeId ID of the user to be unfollowed
     */
    void unfollow(Long followerId, Long followeeId);

    /**
     * Returns how many followers a user has.
     *
     * @param userId ID of the user whose followers to count
     * @return number of users following userId
     */
    long countFollowers(Long userId);

    /**
     * Returns how many users the given user is following.
     *
     * @param userId ID of the user who follows others
     * @return number of users userId is following
     */
    long countFollowing(Long userId);
}