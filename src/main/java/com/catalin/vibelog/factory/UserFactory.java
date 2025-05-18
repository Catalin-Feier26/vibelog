package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.User;

/**
 * Factory for creating User instances of various roles.
 */
public interface UserFactory {

    /**
     * Create a new user with only the required registration fields.
     *
     * @param email         the user's email
     * @param passwordHash  the hashed password
     * @param username      the chosen username
     * @return a User subtype (RegularUser, Moderator, or Admin)
     */
    User create(String email, String passwordHash, String username);

    /**
     * Create a new user with both required and optional profile fields.
     *
     * @param email           the user's email
     * @param passwordHash    the hashed password
     * @param username        the chosen username
     * @param bio             the user's biography (nullable)
     * @param profilePicture  URL or path to profile picture (nullable)
     * @return a User subtype
     */
    User create(String email,
                String passwordHash,
                String username,
                String bio,
                String profilePicture);
}
