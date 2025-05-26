package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.service.AdminAnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analytics;

    public AdminAnalyticsController(AdminAnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/top-liked")
    public PostResponse topLiked() {
        return analytics.topLikedPost();   // May be null if no posts exist
    }

    @GetMapping("/top-commented")
    public PostResponse topCommented() {
        return analytics.topCommentedPost();
    }

    @GetMapping("/top-reblogged")
    public PostResponse topReblogged() {
        return analytics.topRebloggedPost();
    }
}