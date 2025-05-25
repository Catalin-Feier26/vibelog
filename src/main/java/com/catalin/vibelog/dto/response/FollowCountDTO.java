package com.catalin.vibelog.dto.response;

/**
 * Response DTO for follower/following counts of a user.
 *
 * @param followers  total number of followers
 * @param following  total number of users being followed
 */
public record FollowCountDTO(
        long followers, long following
) {
}
