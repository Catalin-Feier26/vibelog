package com.catalin.vibelog.exception;

/**
 * Thrown to indicate that provided authentication credentials are invalid.
 * <p>
 * This exception is typically raised during login when the supplied email/password
 * combination does not match any existing user record.
 * </p>
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining the authentication failure
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
