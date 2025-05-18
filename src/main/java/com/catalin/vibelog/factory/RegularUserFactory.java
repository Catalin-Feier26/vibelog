package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;

/**
 * Creates RegularUser instances.
 */
public class RegularUserFactory implements UserFactory {

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
