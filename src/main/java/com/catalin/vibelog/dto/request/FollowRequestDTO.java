package com.catalin.vibelog.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload for follow/unfollow/state operations, carrying
 * both the acting user’s and the target user’s usernames.
 *
 * @param followerUsername  the user who is following or unfollowing
 * @param followeeUsername  the user to be followed or unfollowed
 */
public record FollowRequestDTO(
        @NotBlank String followerUsername,
        @NotBlank String followeeUsername
) {}