package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.Moderator;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;

/**
 * Factory for creating {@link Moderator} instances with the MODERATOR role.
 * <p>
 * Provides overloaded creation methods to initialize moderators with or without
 * optional profile details such as bio and profile picture.
 * </p>
 */
public class ModeratorFactory implements UserFactory {

    /**
     * Create a new {@link Moderator} with the specified credentials and default
     * (empty) profile information.
     *
     * @param email        the moderator's email address
     * @param passwordHash the hashed password
     * @param username     the chosen username
     * @return a {@link User} instance of type Moderator with no bio or picture
     */
    @Override
    public User create(String email, String passwordHash, String username) {
        return create(email, passwordHash, username, null, null);
    }

    /**
     * Create a new {@link Moderator} with the specified credentials and optional
     * profile details.
     *
     * @param email          the moderator's email address
     * @param passwordHash   the hashed password
     * @param username       the chosen username
     * @param bio            optional biography text (may be null)
     * @param profilePicture optional URL/path to a profile picture (may be null)
     * @return a fully populated {@link User} instance of type Moderator
     */
    @Override
    public User create(String email,
                       String passwordHash,
                       String username,
                       String bio,
                       String profilePicture) {
        Moderator m = new Moderator();
        m.setEmail(email);
        m.setPasswordHash(passwordHash);
        m.setUsername(username);
        m.setRole(Role.MODERATOR);
        m.setBio(bio);
        m.setProfilePicture(profilePicture);
        return m;
    }
}
