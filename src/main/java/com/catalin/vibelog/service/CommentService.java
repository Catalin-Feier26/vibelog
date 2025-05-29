package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.CommentRequest;
import com.catalin.vibelog.dto.response.CommentResponse;
import java.util.List;

/**
 * Defines business operations for creating, retrieving, updating, and deleting comments on posts.
 * Implementations enforce permission checks (author vs. moderator) and handle persistence.
 */
public interface CommentService {

    /**
     * Add a new comment under the given post.
     *
     * @param postId         the ID of the post being commented on
     * @param req            the {@link CommentRequest} containing the comment content
     * @param authorUsername the username of the commenter (from Authentication)
     * @return the created {@link CommentResponse}
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no post exists with the given ID
     * @throws com.catalin.vibelog.exception.UserNotFoundException     if the author username does not exist
     */
    CommentResponse addComment(Long postId, CommentRequest req, String authorUsername);

    /**
     * List all comments for a given post, ordered by creation time ascending.
     *
     * @param postId the ID of the post whose comments to list
     * @return a {@link List} of {@link CommentResponse}
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no post exists with the given ID
     */
    List<CommentResponse> listComments(Long postId);

    /**
     * Update an existing comment. Only the original author may perform this.
     *
     * @param commentId      the ID of the comment to update
     * @param req            the {@link CommentRequest} with the new content
     * @param authorUsername the username of the user attempting the update
     * @return the updated {@link CommentResponse}
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no comment exists with the given ID
     */
    CommentResponse updateComment(Long commentId, CommentRequest req, String authorUsername);

    /**
     * Delete a comment. Only the original author may perform this.
     *
     * @param commentId      the ID of the comment to delete
     * @param authorUsername the username of the user attempting the deletion
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no comment exists with the given ID
     */
    void deleteComment(Long commentId, String authorUsername);

    /**
     * Delete a comment by moderator (admin) privileges.
     * This bypasses author ownership checks and removes the comment entirely.
     *
     * @param commentId the ID of the comment to delete
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no comment exists with the given ID
     */
    void deleteCommentAsModerator(Long commentId);
}
