package com.catalin.vibelog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when no Notification exists with the given ID
 * or the notification does not belong to the current user.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificationNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a message containing the missing ID.
     *
     * @param id the notification ID that was not found
     */
    public NotificationNotFoundException(Long id) {
        super("Notification not found with id " + id);
    }
}
