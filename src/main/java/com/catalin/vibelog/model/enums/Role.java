package com.catalin.vibelog.model.enums;

/**
 * Defines the roles a user may have within the application.
 */
public enum Role {
    /** Regular end-user with standard privileges. */
    USER,
    /** Moderator with permissions to review and flag content. */
    MODERATOR,
    /** Administrator with full system control. */
    ADMIN;

    /**
     * Parses a string to a {@code Role}, case-insensitive.
     *
     * @param role the name of the role
     * @return the corresponding {@code Role}
     * @throws IllegalArgumentException if no matching role is found
     */
    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
