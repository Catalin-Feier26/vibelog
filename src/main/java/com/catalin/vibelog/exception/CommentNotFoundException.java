package com.catalin.vibelog.exception;

/**
 * Thrown when no {@link com.catalin.vibelog.model.Comment} exists with the given ID.
 * Extends {@link ResourceNotFoundException} so that it will be handled as a 404.
 */
public class CommentNotFoundException extends ResourceNotFoundException {

    /**
     * Construct a new exception indicating that the comment was not found.
     *
     * @param commentId the ID of the comment that could not be found
     */
    public CommentNotFoundException(Long commentId) {
        super("Comment not found with id " + commentId);
    }
}
