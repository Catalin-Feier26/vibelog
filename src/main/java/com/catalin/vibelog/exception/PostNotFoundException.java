package com.catalin.vibelog.exception;

/**
 * Thrown when a {@code Post} with the given ID cannot be found.
 */
public class PostNotFoundException extends ResourceNotFoundException {
    /**
     * @param postId the ID of the post that was not found
     */
    public PostNotFoundException(Long postId) {
        super("Post not found with id " + postId);
    }
}
