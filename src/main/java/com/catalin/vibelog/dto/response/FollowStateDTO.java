package com.catalin.vibelog.dto.response;

/**
 * Response DTO indicating whether the current user
 * is following another user.
 *
 * @param following true if following, false otherwise
 */
public record FollowStateDTO (boolean following){
}
