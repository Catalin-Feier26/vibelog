package com.catalin.vibelog.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a "like" action by a user on a post.
 */
@Entity
@Table(name = "likes")
public class Like {

    /** Composite key combining user and post IDs. */
    @EmbeddedId
    private LikeId id;

    /** The user who performed the like. */
    @ManyToOne
    @MapsId("userId")
    private User user;

    /** The post that was liked. */
    @ManyToOne
    @MapsId("postId")
    private Post post;

    /** Timestamp when the like occurred. */
    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;

    /**
     * Default constructor required by JPA.
     */
    public Like() {
    }

    /**
     * Constructs a Like entity linking a user to a post at a given time.
     *
     * @param id      composite key of user and post IDs
     * @param likedAt timestamp of when the like was made
     */
    public Like(LikeId id, LocalDateTime likedAt) {
        this.id = id;
        this.likedAt = likedAt;
    }
}
