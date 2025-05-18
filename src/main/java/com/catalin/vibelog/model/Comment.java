package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a comment made by a user on a post.
 */
@Entity
@Setter
@Getter
@Table(name = "comments")
public class Comment {

    /** Primary key identifier for the comment. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Text content of the comment. */
    @Column(nullable = false, length = 1000)
    private String content;

    /** Timestamp when the comment was created. */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** The user who authored this comment. */
    @ManyToOne(optional = false)
    private User author;

    /** The post to which this comment belongs. */
    @ManyToOne(optional = false)
    private Post post;

    /** Timestamp when the comment was last edited. */
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    /**
     * Lifecycle callback: sets {@code createdAt} to now before first persist.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback: updates {@code editedAt} to now when the comment is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now();
    }

    /** Default no-args constructor required by JPA. */
    public Comment() {}

    /**
     * Entry point for the {@link Builder} to construct a {@code Comment}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for creating {@link Comment} instances.
     */
    public static class Builder {
        private String content;
        private User author;
        private Post post;

        /**
         * Sets the content of the comment.
         *
         * @param content the comment text
         * @return this {@code Builder} instance
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * Sets the author of the comment.
         *
         * @param author the {@link User} who wrote the comment
         * @return this {@code Builder} instance
         */
        public Builder author(User author) {
            this.author = author;
            return this;
        }

        /**
         * Sets the post to which this comment belongs.
         *
         * @param post the {@link Post} being commented on
         * @return this {@code Builder} instance
         */
        public Builder post(Post post) {
            this.post = post;
            return this;
        }

        /**
         * Builds and returns a new {@link Comment} instance.
         *
         * @return the constructed {@code Comment}
         * @throws IllegalStateException if required fields are missing
         */
        public Comment build() {
            if (content == null || content.isBlank()) {
                throw new IllegalStateException("Comment content must be provided");
            }
            if (author == null) {
                throw new IllegalStateException("Comment author must be provided");
            }
            if (post == null) {
                throw new IllegalStateException("Comment post must be provided");
            }
            Comment c = new Comment();
            c.setContent(this.content);
            c.setAuthor(this.author);
            c.setPost(this.post);
            return c;
        }
    }

}
