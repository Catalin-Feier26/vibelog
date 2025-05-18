package com.catalin.vibelog.model;

import com.catalin.vibelog.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class for all user types in the Vibelog application.
 * Provides common fields and relationships for regular users, moderators, and admins.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    /** Primary key identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique email address used for login and notifications. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Hashed password for authentication; stored securely. */
    @Column(name="password_hash" ,nullable = false)
    private String passwordHash;

    /** Unique username displayed in the application. */
    @Column(nullable = false, unique = true)
    private String username;

    /** Role determining the user's permissions (USER, MODERATOR, ADMIN). */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Timestamp when the user was created; set automatically and not updatable. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** URL or path to the user's profile picture. */
    @Column(name = "profile_picture")
    private String profilePicture;

    /** Short biography or description provided by the user. */
    @Column(length = 500)
    private String bio;

    /**
     * Relationships representing users this user is following.
     * Mapped by the 'follower' field in {@link Follow}.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Follow> following = new HashSet<>();

    /**
     * Relationships representing users who follow this user.
     * Mapped by the 'followed' field in {@link Follow}.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Follow> followers = new HashSet<>();

    /**
     * All posts authored by this user.
     * Mapped by the 'author' field in {@link Post}.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    /**
     * Lifecycle callback to set the creation timestamp before persisting.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
