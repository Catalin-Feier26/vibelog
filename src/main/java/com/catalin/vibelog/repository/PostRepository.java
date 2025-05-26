package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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
    // detect if a user reblogged
    boolean existsByAuthorUsernameAndOriginalPostId(String authorUsername, Long originalPostId);

    // count all reblogs (any post with originalPost FK)
    int countByOriginalPostId(Long originalPostId);

    // find the single reblog to delete
    Optional<Post> findByAuthorUsernameAndOriginalPostId(String authorUsername, Long originalPostId);
    /**
     * Search published posts whose title or body contains {@code text}, ignoring case.
     */
    Page<Post> findByStatusAndTitleContainingIgnoreCaseOrStatusAndBodyContainingIgnoreCase(
            PostStatus status1, String titleFragment,
            PostStatus status2, String bodyFragment,
            Pageable pageable
    );
    /** Returns the IDs of the posts sorted by number of likes desc. */
    @Query("""
      SELECT p.id
      FROM Post p
      LEFT JOIN p.likes l
      GROUP BY p.id
      ORDER BY COUNT(l) DESC
      """)
    List<Long> findTopLikedPostIds(Pageable limit);

    /** Returns the IDs of the posts sorted by number of comments desc. */
    @Query("""
      SELECT p.id
      FROM Post p
      LEFT JOIN p.comments c
      GROUP BY p.id
      ORDER BY COUNT(c) DESC
      """)
    List<Long> findTopCommentedPostIds(Pageable limit);

    /**
     * Returns the IDs of the original posts sorted by how many times they’ve been reblogged.
     * We do a sub‐select over Post to count how many reference p as originalPost.
     */
    @Query("""
      SELECT p.id
      FROM Post p
      ORDER BY (SELECT COUNT(r) FROM Post r WHERE r.originalPost = p) DESC
      """)
    List<Long> findTopRebloggedPostIds(Pageable limit);
}
