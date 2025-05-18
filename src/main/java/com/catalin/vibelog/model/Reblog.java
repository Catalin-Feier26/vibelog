package com.catalin.vibelog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a repost (reblog) action by a user on an existing post.
 */
@Entity
@Table(name = "reblogs")
public class Reblog {

    /** Primary key identifier for the reblog record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who performed the reblog action. */
    @ManyToOne
    private User user;

    /** The original post that was reblogged. */
    @ManyToOne
    private Post originalPost;

    /** Timestamp when the post was reblogged. */
    @Column(nullable = false)
    private LocalDateTime rebloggedAt;
}
