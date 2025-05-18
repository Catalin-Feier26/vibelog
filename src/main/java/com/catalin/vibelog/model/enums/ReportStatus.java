package com.catalin.vibelog.model.enums;

/**
 * Represents the current lifecycle state of a content report.
 */
public enum ReportStatus {
    /** Report has been submitted and awaits moderator review. */
    PENDING,
    /** Report has been reviewed by a moderator but no final action recorded. */
    REVIEWED,
    /** Report has been resolved (action taken or dismissed). */
    RESOLVED
}
