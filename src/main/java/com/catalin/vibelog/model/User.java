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
 * Abstract base entity representing a user in the Vibelog application.
 * <p>
 * Contains common attributes and relationships for all user types,
 * including authentication credentials, profile information,
 * and associations to posts, comments, likes, follows, reports, and notifications.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    /**
     * Primary key identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique email address used for login and notifications.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Securely hashed password for authentication.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Unique username displayed in the application.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Role determining the user's permissions (e.g., USER, MODERATOR, ADMIN).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Timestamp when the user was created; set automatically before persisting.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * URL or path to the user's profile picture.
     */
    @Column(name = "profile_picture")
    private String profilePicture;

    /**
     * Short biography or description provided by the user.
     */
    @Column(length = 500)
    private String bio;

    /**
     * Follows initiated by this user (users this user is following).
     * Cascade operations and orphan removal ensure consistency.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Follow> following = new HashSet<>();

    /**
     * Follows targeting this user (users who follow this user).
     */
    @JsonIgnore
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Follow> followers = new HashSet<>();

    /**
     * Posts authored by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    /**
     * Likes made by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    /**
     * Comments authored by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    /**
     * Reports filed by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();

    /**
     * Notifications sent to this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    /**
     * Lifecycle callback to set the creation timestamp before persisting.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}