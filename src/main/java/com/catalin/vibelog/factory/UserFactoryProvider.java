package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.enums.Role;

/**
 * Returns the correct UserFactory based on role.
 */
public class UserFactoryProvider {
    public static UserFactory getFactory(Role role) {
        return switch (role) {
            case USER      -> new RegularUserFactory();
            case MODERATOR -> new ModeratorFactory();
            case ADMIN     -> new AdminFactory();
        };
    }
}
