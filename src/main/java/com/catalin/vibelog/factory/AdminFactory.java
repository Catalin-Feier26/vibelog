package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.Admin;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.Role;

/**
 * Creates Admin instances.
 */
public class AdminFactory implements UserFactory {

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
