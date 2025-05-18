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
 * Supports text content, media attachments, optional reblog linkage, and tags.
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

    /** Current status of the post (draft or published). */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    /** Timestamp when the post was first created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the post was last updated. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** The user who authored this post. */
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** Media items attached to this post. */
    @ManyToMany
    @JoinTable(
            name = "posts_media",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> mediaList = new ArrayList<>();

    /** Reference to the original post if this is a reblog. */
    @ManyToOne
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    /** Tags associated with this post for search and filtering. */
    @ElementCollection
    private List<String> tags = new ArrayList<>();

    /** All comments on this post; deleted when the post is deleted. */
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    /** All like records for this post; deleted when the post is deleted. */
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    /** All reblog records of this post; deleted when the post is deleted. */
    @OneToMany(mappedBy = "originalPost",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Reblog> reblogs = new ArrayList<>();

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
     * Entry point for the {@link Builder} to construct a {@code Post}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for creating {@link Post} instances.
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
         * @return this {@code Builder} instance
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the body content of the post.
         *
         * @param body the post body
         * @return this {@code Builder} instance
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the status of the post.
         *
         * @param status the post status
         * @return this {@code Builder} instance
         */
        public Builder status(PostStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the author of the post.
         *
         * @param author the {@link User} who authored the post
         * @return this {@code Builder} instance
         */
        public Builder author(User author) {
            this.author = author;
            return this;
        }

        /**
         * Replaces the media list for the post.
         *
         * @param mediaList the list of {@link Media} items
         * @return this {@code Builder} instance
         */
        public Builder mediaList(List<Media> mediaList) {
            this.mediaList = mediaList;
            return this;
        }

        /**
         * Adds a single media item to the post.
         *
         * @param media the {@link Media} item
         * @return this {@code Builder} instance
         */
        public Builder addMedia(Media media) {
            this.mediaList.add(media);
            return this;
        }

        /**
         * Sets the original post for a reblog.
         *
         * @param originalPost the original {@link Post}
         * @return this {@code Builder} instance
         */
        public Builder originalPost(Post originalPost) {
            this.originalPost = originalPost;
            return this;
        }

        /**
         * Replaces the tag list for the post.
         *
         * @param tags the list of tag strings
         * @return this {@code Builder} instance
         */
        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Adds a single tag to the post.
         *
         * @param tag the tag string
         * @return this {@code Builder} instance
         */
        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        /**
         * Builds and returns a new {@link Post} instance.
         *
         * @return the constructed {@code Post}
         * @throws IllegalStateException if required fields are missing
         */
        public Post build() {
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
