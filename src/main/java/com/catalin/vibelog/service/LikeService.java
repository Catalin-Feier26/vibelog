package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.response.LikeResponse;

/**
 * Business operations around liking/unliking posts.
 */
public interface LikeService {

    /**
     * Toggle a like for the current user on the given post.
     * If the user has not yet liked the post, this will create a like; otherwise it will remove it.
     *
     * @param postId   the ID of the post to like or unlike
     * @param username the username of the liker (from Authentication)
     * @return a {@link LikeResponse} containing the new total like count and the user's like status
     */
    LikeResponse toggleLike(Long postId, String username);

    /**
     * Count how many total likes a post has.
     *
     * @param postId the ID of the post
     * @return the total number of likes
     */
    int countLikes(Long postId);

    /**
     * Check whether the given user has liked the specified post.
     *
     * @param postId   the ID of the post
     * @param username the username to check for
     * @return {@code true} if the user has already liked the post
     */
    boolean isLiked(Long postId, String username);
}
