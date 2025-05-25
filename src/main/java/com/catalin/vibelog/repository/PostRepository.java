package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Post} entities.
 * Provides CRUD operations plus custom queries for pagination and filtering by status or author.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Retrieve a page of posts filtered by {@link PostStatus}.
     *
     * @param status   the {@link PostStatus} to filter by (e.g. PUBLISHED or DRAFT)
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Post} matching the given status
     */
    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    /**
     * Retrieve a page of posts authored by a specific user.
     *
     * @param username the author's username
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Post} created by the given username
     */
    Page<Post> findByAuthorUsername(String username, Pageable pageable);
    Page<Post> findByAuthorUsernameAndStatus(String username, PostStatus status, Pageable pageable);

}
