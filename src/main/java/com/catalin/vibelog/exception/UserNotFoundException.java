package com.catalin.vibelog.exception;

/**
 * Exception thrown when a User with the given username cannot be found.
 */
public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UserNotFoundException for the given username.
     *
     * @param username the username that was not found
     */
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}
