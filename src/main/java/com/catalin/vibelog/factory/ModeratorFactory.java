package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.Moderator;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;

/**
 * Creates Moderator instances.
 */
public class ModeratorFactory implements UserFactory {

    @Override
    public User create(String email, String passwordHash, String username) {
        return create(email, passwordHash, username, null, null);
    }

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
