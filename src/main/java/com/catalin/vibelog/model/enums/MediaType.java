package com.catalin.vibelog.model.enums;

/**
 * Represents supported media types for uploads and posts.
 */
public enum MediaType {
    /** Image media (e.g., JPEG, PNG). */
    IMG,
    /** Video media (e.g., MP4, AVI). */
    VIDEO;

    /**
     * Parses a string to a {@code MediaType}, case-insensitive.
     *
     * @param type the name of the media type
     * @return the corresponding {@code MediaType}
     * @throws IllegalArgumentException if no matching type is found
     */
    public static MediaType fromString(String type) {
        return MediaType.valueOf(type.toUpperCase());
    }
}
