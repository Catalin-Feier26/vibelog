package com.catalin.vibelog.controller;

import com.catalin.vibelog.service.CommentService;
import com.catalin.vibelog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderation")
@PreAuthorize("hasRole('MODERATOR')")
public class ModeratorController {

    private final PostService postService;
    private final CommentService commentService;

    public ModeratorController(PostService postService,
                               CommentService commentService) {
        this.postService    = postService;
        this.commentService = commentService;
    }

    /** DELETE /api/moderation/posts/{postId} */
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        postService.deletePostAsModerator(postId);
    }

    /** DELETE /api/moderation/comments/{commentId} */
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentAsModerator(commentId);
    }
}
