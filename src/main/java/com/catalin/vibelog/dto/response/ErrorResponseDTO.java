package com.catalin.vibelog.dto.response;

import java.time.Instant;

/**
 * Standard error response for failed requests.
 *
 * @param timestamp  when the error occurred (ISO-8601 string or {@link java.time.Instant})
 * @param status     the HTTP status code
 * @param error      a short error description
 * @param message    a more detailed message for the client
 * @param path       the request path that caused the error
 */
public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}