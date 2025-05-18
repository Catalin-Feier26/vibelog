package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User} entities.
 * Provides basic CRUD operations and lookup methods needed for authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Check whether a user with the given email already exists.
     *
     * @param email the email address to check
     * @return true if a user with that email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return true if a user with that username exists
     */
    boolean existsByUsername(String username);

    /**
     * Find a user by their email address.
     * Used primarily during login.
     *
     * @param email the email to search for
     * @return an Optional containing the matching User, if any
     */
    Optional<User> findByEmail(String email);

    /**
     * (Optional) Find a user by their username.
     * May be useful if you allow login by username later.
     *
     * @param username the username to search for
     * @return an Optional containing the matching User, if any
     */
    Optional<User> findByUsername(String username);
}
