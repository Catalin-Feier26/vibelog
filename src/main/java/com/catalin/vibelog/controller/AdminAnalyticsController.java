package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.service.AdminAnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin‐only REST controller for retrieving analytics on post engagement.
 * <p>
 * Secured with {@code @PreAuthorize("hasRole('ADMIN')")}, exposing endpoints
 * to fetch the single top‐performing post by likes, comments, or reblogs.
 * </p>
 */
@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analytics;

    /**
     * Constructs the AdminAnalyticsController with the required analytics service.
     *
     * @param analytics the service providing engagement‐based rankings of posts
     */
    public AdminAnalyticsController(AdminAnalyticsService analytics) {
        this.analytics = analytics;
    }

    /**
     * GET /api/admin/analytics/top-liked
     * <p>
     * Retrieve the single most‐liked post.
     * If no posts exist, this may return {@code null}.
     * </p>
     *
     * @return a {@link PostResponse} representing the most liked post, or {@code null} if none
     */
    @GetMapping("/top-liked")
    public PostResponse topLiked() {
        return analytics.topLikedPost();
    }

    /**
     * GET /api/admin/analytics/top-commented
     * <p>
     * Retrieve the single most‐commented post.
     * If no posts exist, this may return {@code null}.
     * </p>
     *
     * @return a {@link PostResponse} representing the most commented post, or {@code null} if none
     */
    @GetMapping("/top-commented")
    public PostResponse topCommented() {
        return analytics.topCommentedPost();
    }

    /**
     * GET /api/admin/analytics/top-reblogged
     * <p>
     * Retrieve the single most‐reblogged post.
     * If no posts exist, this may return {@code null}.
     * </p>
     *
     * @return a {@link PostResponse} representing the most reblogged post, or {@code null} if none
     */
    @GetMapping("/top-reblogged")
    public PostResponse topReblogged() {
        return analytics.topRebloggedPost();
    }
}
