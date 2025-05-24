package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.LikeResponse;
import com.catalin.vibelog.service.LikeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing likes on posts.
 * <p>
 * Exposes endpoints to toggle a like and to fetch like status/count.
 * All operations require an authenticated user.
 * </p>
 */
@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * Toggle a like for the current user on the given post.
     * If the user has not yet liked the post, this will create a like;
     * otherwise it will remove it.
     *
     * @param postId the ID of the post to like or unlike
     * @param auth   the Spring Security {@link Authentication} of the current user
     * @return a {@link LikeResponse} containing the new total like count and the user's like status
     */
    @PostMapping("/api/posts/{postId}/likes")
    public LikeResponse toggleLike(
            @PathVariable Long postId,
            Authentication auth
    ) {
        String username = auth.getName();
        return likeService.toggleLike(postId, username);
    }

    /**
     * Fetch the current user's like status and total like count for a post.
     *
     * @param postId the ID of the post
     * @param auth   the Spring Security {@link Authentication} of the current user
     * @return a {@link LikeResponse} with current like count and whether the user has liked
     */
    @GetMapping("/api/posts/{postId}/likes")
    public LikeResponse getLikeStatus(
            @PathVariable Long postId,
            Authentication auth
    ) {
        String username = auth.getName();
        int totalLikes = likeService.countLikes(postId);
        boolean liked  = likeService.isLiked(postId, username);
        return new LikeResponse(postId, liked, totalLikes);
    }
}
