package com.catalin.vibelog.exception;

/**
 * Thrown to indicate that a requested email address is already in use.
 * <p>
 * This exception is typically raised during user registration or profile update
 * when the desired email conflicts with an existing account.
 * </p>
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining which email was already in use
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
