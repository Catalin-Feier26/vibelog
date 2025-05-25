package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Reblog;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Reblog} entities, which represent
 * “repost” actions by users on existing posts.
 */
@Repository
public interface ReblogRepository extends JpaRepository<Reblog, Long> {

    /**
     * Determine whether a given user has already reblogged a specific post.
     *
     * @param user         the {@link User} who may have performed the reblog
     * @param originalPost the original {@link Post} in question
     * @return {@code true} if the user has reblogged the post, {@code false} otherwise
     */
    boolean existsByUserAndOriginalPost(User user, Post originalPost);

    /**
     * Retrieve a paginated list of reblogs made by a given user.
     *
     * @param username the username of the reblogger
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Reblog} entities for the specified user
     */
    Page<Reblog> findByUserUsername(String username, Pageable pageable);

    /**
     * Retrieve a paginated list of reblogs of a specific original post.
     *
     * @param postId   the ID of the original post
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link Reblog} entities for the specified post
     */
    Page<Reblog> findByOriginalPostId(Long postId, Pageable pageable);
}
