package com.catalin.vibelog.factory;

import com.catalin.vibelog.model.enums.Role;

/**
 * Provider for {@link UserFactory} implementations based on user roles.
 * <p>
 * Delegates to the appropriate factory to create users with role-specific defaults
 * (e.g., regular users, moderators, or administrators).
 * </p>
 */
public class UserFactoryProvider {

    /**
     * Obtain a {@link UserFactory} corresponding to the given role.
     *
     * @param role the {@link Role} determining which factory to return
     * @return a concrete {@link UserFactory} for creating users with the specified role
     * @throws IllegalArgumentException if the role is null
     */
    public static UserFactory getFactory(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        return switch (role) {
            case USER      -> new RegularUserFactory();
            case MODERATOR -> new ModeratorFactory();
            case ADMIN     -> new AdminFactory();
        };
    }
}
