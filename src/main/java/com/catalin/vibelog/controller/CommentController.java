package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.CommentRequest;
import com.catalin.vibelog.dto.response.CommentResponse;
import com.catalin.vibelog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing comments on posts.
 * <p>
 * Exposes endpoints to list, create, update, and delete comments.
 * All mutating operations require an authenticated user.
 * </p>
 */
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * List all comments for a given post, ordered by creation time ascending.
     *
     * @param postId the ID of the post whose comments to retrieve
     * @return a {@link List} of {@link CommentResponse}
     */
    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentResponse> listComments(@PathVariable Long postId) {
        return commentService.listComments(postId);
    }

    /**
     * Add a new comment under a post.
     *
     * @param postId the ID of the post to comment on
     * @param auth   the Spring Security {@link Authentication} of the current user
     * @param req    the {@link CommentRequest} carrying the comment content
     * @return the created {@link CommentResponse}
     */
    @PostMapping("/api/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @PathVariable Long postId,
            Authentication auth,
            @Valid @RequestBody CommentRequest req
    ) {
        String username = auth.getName();
        return commentService.addComment(postId, req, username);
    }

    /**
     * Update an existing comment. Only the comment’s author may perform this.
     *
     * @param commentId the ID of the comment to update
     * @param auth      the Spring Security {@link Authentication} of the current user
     * @param req       the {@link CommentRequest} with the new content
     * @return the updated {@link CommentResponse}
     */
    @PutMapping("/api/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            Authentication auth,
            @Valid @RequestBody CommentRequest req
    ) {
        String username = auth.getName();
        return commentService.updateComment(commentId, req, username);
    }

    /**
     * Delete a comment. Only the comment’s author may perform this.
     *
     * @param commentId the ID of the comment to delete
     * @param auth      the Spring Security {@link Authentication} of the current user
     */
    @DeleteMapping("/api/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long commentId,
            Authentication auth
    ) {
        String username = auth.getName();
        commentService.deleteComment(commentId, username);
    }
}
