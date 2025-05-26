package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.response.PostResponse;

public interface AdminAnalyticsService {
    /** Full PostResponse for the single most‐liked post, or null. */
    PostResponse topLikedPost();
    /** Full PostResponse for the single most‐commented post, or null. */
    PostResponse topCommentedPost();
    /** Full PostResponse for the single most-reblogged post, or null. */
    PostResponse topRebloggedPost();
}
