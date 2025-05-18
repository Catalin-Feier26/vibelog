package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Represents a "follow" relationship between two users.
 * Tracks which user is following which other user and when the follow occurred.
 */
@Getter
@Setter
@Entity
@Table(name = "follows")
public class Follow {

    /** Composite primary key of the follow relationship. */
    @EmbeddedId
    private FollowId id;

    /** The user who is doing the following. */
    @ManyToOne
    @MapsId("followingUserId")
    @JoinColumn(name = "following_user_id", nullable = false)
    private User follower;

    /** The user who is being followed. */
    @ManyToOne
    @MapsId("followedUserId")
    @JoinColumn(name = "followed_user_id", nullable = false)
    private User followed;

    /** Timestamp when the follow relationship was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructs an empty Follow instance.
     */
    public Follow() {
    }

    /**
     * Lifecycle callback: sets {@code createdAt} to now before persisting.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
