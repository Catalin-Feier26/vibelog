package com.catalin.vibelog.exception;

/**
 * Exception thrown when an expected Reblog record is not found.
 */
public class ReblogNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new ReblogNotFoundException.
     *
     * @param reblogId the ID of the missing reblog record
     */
    public ReblogNotFoundException(Long reblogId) {
        super("Reblog not found with id " + reblogId);
    }
}