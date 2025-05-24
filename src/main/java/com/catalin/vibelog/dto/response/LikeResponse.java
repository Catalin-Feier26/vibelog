package com.catalin.vibelog.dto.response;

/**
 * DTO representing the like state for a post.
 * <p>
 * Indicates whether the current user has liked the post, and the total likes count.
 * </p>
 *
 * @param postId    identifier of the liked post
 * @param liked     true if the current user has liked this post
 * @param totalLikes the total number of likes on the post
 */
public record LikeResponse(
        Long postId,
        boolean liked,
        int totalLikes
) {}
