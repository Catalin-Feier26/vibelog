package com.catalin.vibelog.exception;

/**
 * Thrown to indicate that a requested username is already in use.
 * <p>
 * This exception is typically raised during user registration or profile update
 * when the desired username conflicts with an existing account.
 * </p>
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the cause
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
