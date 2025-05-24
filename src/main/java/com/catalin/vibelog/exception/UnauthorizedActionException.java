package com.catalin.vibelog.exception;

/**
 * Thrown when the current user attempts an operation
 * they are not authorized to perform (e.g. editing someone else’s post).
 */
public class UnauthorizedActionException extends RuntimeException {
    /**
     * @param message a description of the unauthorized action
     */
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
