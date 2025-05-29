package com.catalin.vibelog.exception;

/**
 * Thrown to indicate that a requested resource could not be found.
 * <p>
 * This exception is typically raised when an entity lookup by ID or other identifier
 * fails— for example, when no user, post, comment, or other domain object exists
 * with the specified identifier.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
