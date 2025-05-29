package com.catalin.vibelog.controller;

import com.catalin.vibelog.service.CommentService;
import com.catalin.vibelog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for moderator-only actions.
 * <p>
 * Secured with {@code @PreAuthorize("hasRole('MODERATOR')")} to allow only
 * users with the MODERATOR role to invoke these endpoints.
 * </p>
 */
@RestController
@RequestMapping("/api/moderation")
@PreAuthorize("hasRole('MODERATOR')")
public class ModeratorController {

    private final PostService postService;
    private final CommentService commentService;

    /**
     * Constructs the ModeratorController with required services for moderation.
     *
     * @param postService    service providing post deletion capabilities
     * @param commentService service providing comment deletion capabilities
     */
    public ModeratorController(PostService postService,
                               CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * DELETE /api/moderation/posts/{postId}
     * <p>
     * Permanently deletes a post and all associated data (comments, likes, media, reports).
     * Only accessible to users with the MODERATOR role.
     * </p>
     *
     * @param postId the ID of the post to be deleted by a moderator
     * @throws com.catalin.vibelog.exception.PostNotFoundException if no post exists with the given ID
     */
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        postService.deletePostAsModerator(postId);
    }

    /**
     * DELETE /api/moderation/comments/{commentId}
     * <p>
     * Permanently deletes a comment and all associated reports.
     * Only accessible to users with the MODERATOR role.
     * </p>
     *
     * @param commentId the ID of the comment to be deleted by a moderator
     * @throws com.catalin.vibelog.exception.CommentNotFoundException if no comment exists with the given ID
     */
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentAsModerator(commentId);
    }
}
