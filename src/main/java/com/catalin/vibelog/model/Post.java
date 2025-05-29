package com.catalin.vibelog.model;

import com.catalin.vibelog.model.enums.PostStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user-created post in the Vibelog platform.
 * <p>
 * Supports text content, media attachments, optional reblog linkage,
 * tags, comments, likes, and lifecycle timestamps for creation and updates.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    /** Primary key identifier for the post. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Title of the post. */
    @Column(nullable = false)
    private String title;

    /** Main body content of the post. */
    @Column(nullable = false)
    private String body;

    /** Current status of the post (e.g., DRAFT or PUBLISHED). */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    /** Timestamp when the post was first created; set before persist. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the post was last updated; set before update. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** The user who authored this post. */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** Media items attached to this post. */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> mediaList = new ArrayList<>();

    /** Reference to the original post if this is a reblog; null otherwise. */
    @ManyToOne
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    /**
     * All posts that reblog this post.
     * Deleting this post will cascade‐remove every reblogged copy.
     */
    @OneToMany(mappedBy = "originalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> rebloggedPosts = new ArrayList<>();

    /** Tags associated with this post for search and filtering. */
    @ElementCollection
    private List<String> tags = new ArrayList<>();

    /** All comments on this post; deleted when the post is deleted. */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    /** All like records for this post; deleted when the post is deleted. */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    /**
     * Lifecycle callback to set {@code createdAt} before the entity is persisted.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback to update {@code updatedAt} before the entity is updated.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Entry point for the {@link Builder} to construct a {@code Post} instance.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for creating {@link Post} instances with required and optional fields.
     */
    public static class Builder {
        private String title;
        private String body;
        private PostStatus status = PostStatus.DRAFT;
        private User author;
        private List<Media> mediaList = new ArrayList<>();
        private Post originalPost;
        private List<String> tags = new ArrayList<>();

        /**
         * Sets the title of the post.
         *
         * @param title the post title
         * @return this {@code Builder}
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the body content of the post.
         *
         * @param body the post body
         * @return this {@code Builder}
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the status of the post.
         *
         * @param status the post status
         * @return this {@code Builder}
         */
        public Builder status(PostStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the author of the post.
         *
         * @param author the {@link User} author
         * @return this {@code Builder}
         */
        public Builder author(User author) {
            this.author = author;
            return this;
        }

        /**
         * Replaces the media list for the post.
         *
         * @param mediaList list of media attachments
         * @return this {@code Builder}
         */
        public Builder mediaList(List<Media> mediaList) {
            this.mediaList = mediaList;
            return this;
        }

        /**
         * Adds a single media item to the post.
         *
         * @param media the {@link Media} attachment
         * @return this {@code Builder}
         */
        public Builder addMedia(Media media) {
            this.mediaList.add(media);
            return this;
        }

        /**
         * Sets the original post for a reblog.
         *
         * @param originalPost the original {@link Post}
         * @return this {@code Builder}
         */
        public Builder originalPost(Post originalPost) {
            this.originalPost = originalPost;
            return this;
        }

        /**
         * Replaces the tag list for the post.
         *
         * @param tags list of tag strings
         * @return this {@code Builder}
         */
        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Adds a single tag to the post.
         *
         * @param tag the tag string
         * @return this {@code Builder}
         */
        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        /**
         * Builds and returns a new {@link Post} instance.
         *
         * @return the constructed {@code Post}
         * @throws IllegalStateException if required fields (title, body, author) are missing
         */
        public Post build() {
            if (title == null || body == null || author == null) {
                throw new IllegalStateException("Title, body, and author are required");
            }
            Post p = new Post();
            p.setTitle(this.title);
            p.setBody(this.body);
            p.setStatus(this.status);
            p.setAuthor(this.author);
            p.setMediaList(this.mediaList);
            p.setOriginalPost(this.originalPost);
            p.setTags(this.tags);
            return p;
        }
    }
}
