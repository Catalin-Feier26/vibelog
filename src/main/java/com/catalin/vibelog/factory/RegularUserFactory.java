package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;

/**
 * Factory for creating {@link RegularUser} instances with the USER role.
 * <p>
 * Provides overloaded creation methods for users with or without initial
 * profile details (bio and profile picture).
 * </p>
 */
public class RegularUserFactory implements UserFactory {

    /**
     * Create a new {@link RegularUser} with the given credentials and default
     * (empty) profile information.
     *
     * @param email         the user's email address
     * @param passwordHash  the hashed password
     * @param username      the chosen username
     * @return a {@link User} instance with the USER role and no bio or picture
     */
    @Override
    public User create(String email, String passwordHash, String username) {
        return create(email, passwordHash, username, null, null);
    }

    /**
     * Create a new {@link RegularUser} with the given credentials and optional
     * profile information.
     *
     * @param email           the user's email address
     * @param passwordHash    the hashed password
     * @param username        the chosen username
     * @param bio             optional biography text (may be null)
     * @param profilePicture  optional URL/path to a profile picture (may be null)
     * @return a fully populated {@link User} instance with the USER role
     */
    @Override
    public User create(String email,
                       String passwordHash,
                       String username,
                       String bio,
                       String profilePicture) {
        RegularUser u = new RegularUser();
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setUsername(username);
        u.setRole(Role.USER);
        u.setBio(bio);
        u.setProfilePicture(profilePicture);
        return u;
    }
}
