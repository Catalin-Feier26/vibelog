// src/main/java/com/catalin/vibelog/exception/ReportNotFoundException.java
package com.catalin.vibelog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when no {@link com.catalin.vibelog.model.Report}
 * exists with the given ID.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {

    /**
     * Constructs a new ReportNotFoundException with a detailed message.
     *
     * @param reportId the ID of the report that was not found
     */
    public ReportNotFoundException(Long reportId) {
        super("Report not found with id " + reportId);
    }
}
