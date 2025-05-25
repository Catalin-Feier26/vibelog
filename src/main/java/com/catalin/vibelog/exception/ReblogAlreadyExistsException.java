package com.catalin.vibelog.exception;

/**
 * Exception thrown when a user attempts to reblog
 * a post they have already reblogged.
 */
public class ReblogAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new exception with a detail message that includes
     * the username and original post ID.
     *
     * @param username the user who tried to reblog
     * @param postId   the ID of the post they attempted to reblog
     */
    public ReblogAlreadyExistsException(String username, Long postId) {
        super("User '" + username + "' has already reblogged post with ID " + postId);
    }
}
