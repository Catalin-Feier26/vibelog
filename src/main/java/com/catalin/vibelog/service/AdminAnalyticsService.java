package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.response.PostResponse;

/**
 * Provides administrative analytics operations for posts, such as retrieving
 * the top-performing posts by various engagement metrics.
 */
public interface AdminAnalyticsService {

    /**
     * Retrieve the post with the highest number of likes.
     *
     * @return a {@link PostResponse} representing the most‐liked post,
     *         or {@code null} if there are no posts
     */
    PostResponse topLikedPost();

    /**
     * Retrieve the post with the highest number of comments.
     *
     * @return a {@link PostResponse} representing the most‐commented post,
     *         or {@code null} if there are no posts
     */
    PostResponse topCommentedPost();

    /**
     * Retrieve the post with the highest number of reblogs (shares).
     *
     * @return a {@link PostResponse} representing the most‐reblogged post,
     *         or {@code null} if there are no posts
     */
    PostResponse topRebloggedPost();
}
