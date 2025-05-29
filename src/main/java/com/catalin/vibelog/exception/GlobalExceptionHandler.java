package com.catalin.vibelog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception handler for REST controllers in the Vibelog application.
 * <p>
 * Maps specific application exceptions to appropriate HTTP status codes and response bodies.
 * Defines handlers for email conflict, authentication failures, and a general fallback.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle cases where an email is already registered.
     *
     * @param ex the {@link EmailAlreadyExistsException} containing details
     * @return a {@link ResponseEntity} with HTTP 409 Conflict and the exception message
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handle authentication failures due to invalid credentials.
     *
     * @param ex the {@link InvalidCredentialsException} containing details
     * @return a {@link ResponseEntity} with HTTP 401 Unauthorized and the exception message
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    /**
     * Catch-all handler for any other uncaught exceptions.
     *
     * @param ex the generic {@link Exception} that was thrown
     * @return a {@link ResponseEntity} with HTTP 500 Internal Server Error and a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong.");
    }
}
