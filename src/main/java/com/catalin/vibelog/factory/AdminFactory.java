package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.Admin;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;

/**
 * Factory for creating {@link Admin} instances with the ADMIN role.
 * <p>
 * Provides overloaded creation methods to initialize admins with or without
 * optional profile details such as bio and profile picture.
 * </p>
 */
public class AdminFactory implements UserFactory {

    /**
     * Create a new {@link Admin} with the specified credentials and default
     * (empty) profile information.
     *
     * @param email        the admin's email address
     * @param passwordHash the hashed password
     * @param username     the chosen username
     * @return a {@link User} instance of type Admin with no bio or picture
     */
    @Override
    public User create(String email, String passwordHash, String username) {
        return create(email, passwordHash, username, null, null);
    }

    /**
     * Create a new {@link Admin} with the specified credentials and optional
     * profile details.
     *
     * @param email          the admin's email address
     * @param passwordHash   the hashed password
     * @param username       the chosen username
     * @param bio            optional biography text (may be null)
     * @param profilePicture optional URL/path to a profile picture (may be null)
     * @return a fully populated {@link User} instance of type Admin
     */
    @Override
    public User create(String email,
                       String passwordHash,
                       String username,
                       String bio,
                       String profilePicture) {
        Admin a = new Admin();
        a.setEmail(email);
        a.setPasswordHash(passwordHash);
        a.setUsername(username);
        a.setRole(Role.ADMIN);
        a.setBio(bio);
        a.setProfilePicture(profilePicture);
        return a;
    }
}
