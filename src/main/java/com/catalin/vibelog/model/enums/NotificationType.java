package com.catalin.vibelog.model.enums;

/**
 * Types of notifications that can be sent to users.
 */
public enum NotificationType {
    /** Notification for a post being liked. */
    LIKE,
    /** Notification for a comment on a post. */
    COMMENT,
    /** Notification for a new follower. */
    FOLLOW,
    /** Notification for a post being reblogged. */
    REBLOG,
    /** Report has been resolved by a moderator. */
    REPORT
}
