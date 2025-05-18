package com.catalin.vibelog.model.enums;

/**
 * Possible statuses for a post within the system.
 */
public enum PostStatus {
    /** The post is a draft and not publicly visible. */
    DRAFT,
    /** The post has been published and is visible to users. */
    PUBLISHED;

    /**
     * Parses a string to a {@code PostStatus}, case-insensitive.
     *
     * @param status the name of the post status
     * @return the corresponding {@code PostStatus}
     * @throws IllegalArgumentException if no matching status is found
     */
    public static PostStatus fromString(String status) {
        return PostStatus.valueOf(status.toUpperCase());
    }
}
