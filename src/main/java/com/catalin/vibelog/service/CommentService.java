package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.CommentRequest;
import com.catalin.vibelog.dto.response.CommentResponse;
import java.util.List;

/**
 * Business operations around comments on posts.
 */
public interface CommentService {

    /**
     * Add a new comment under the given post.
     *
     * @param postId         the ID of the post being commented on
     * @param req            the {@link CommentRequest} containing the comment content
     * @param authorUsername the username of the commenter (from Authentication)
     * @return the created {@link CommentResponse}
     */
    CommentResponse addComment(Long postId, CommentRequest req, String authorUsername);

    /**
     * List all comments for a given post, ordered by creation time ascending.
     *
     * @param postId the ID of the post whose comments to list
     * @return a {@link List} of {@link CommentResponse}
     */
    List<CommentResponse> listComments(Long postId);

    /**
     * Update an existing comment. Only the original author may perform this.
     *
     * @param commentId      the ID of the comment to update
     * @param req            the {@link CommentRequest} with the new content
     * @param authorUsername the username of the user attempting the update
     * @return the updated {@link CommentResponse}
     */
    CommentResponse updateComment(Long commentId, CommentRequest req, String authorUsername);

    /**
     * Delete a comment. Only the original author may perform this.
     *
     * @param commentId      the ID of the comment to delete
     * @param authorUsername the username of the user attempting the deletion
     */
    void deleteComment(Long commentId, String authorUsername);
}
