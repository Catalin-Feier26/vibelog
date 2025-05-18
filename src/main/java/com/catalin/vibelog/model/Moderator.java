package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A moderator user who can review and manage content.
 * <p>
 * Tracks the number of reports reviewed and the set of posts
 * this moderator has acted upon.
 */
@Getter
@Setter
@Entity
@Table(name = "moderators")
public class Moderator extends User {

    /**
     * Total number of content reports this moderator has processed.
     */
    @Column(name = "reports_reviewed", nullable = false)
    private int reportsReviewed = 0;

    /**
     * The posts that this moderator has reviewed or moderated.
     */
    @ManyToMany
    @JoinTable(
            name = "moderator_moderated_posts",
            joinColumns = @JoinColumn(name = "moderator_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> moderatedPosts = new HashSet<>();

    /**
     * Constructs a new Moderator with no reviews performed.
     */
    public Moderator() {
    }
}
