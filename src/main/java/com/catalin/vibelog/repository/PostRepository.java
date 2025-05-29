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
 * Spring Data repository for managing {@link Post} entities.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations and defines
 * additional methods for filtering by status, author, search queries,
 * engagement metrics, and reblog relationships.
 * </p>
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Retrieve a page of posts filtered by their status.
     *
     * @param status   the {@link PostStatus} to filter by (e.g., PUBLISHED or DRAFT)
     * @param pageable pagination and sorting information
     * @return a {@link Page} of posts matching the specified status
     */
    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    /**
     * Retrieve a page of posts authored by the specified user.
     *
     * @param username the author's username
     * @param pageable pagination and sorting information
     * @return a {@link Page} of posts created by the given username
     */
    Page<Post> findByAuthorUsername(String username, Pageable pageable);

    /**
     * Retrieve a page of posts authored by the specified user, further
     * filtered by status.
     *
     * @param username the author's username
     * @param status   the {@link PostStatus} to filter by
     * @param pageable pagination and sorting information
     * @return a {@link Page} of posts matching the author and status
     */
    Page<Post> findByAuthorUsernameAndStatus(String username, PostStatus status, Pageable pageable);

    /**
     * Check if a user has reblogged a specific post.
     *
     * @param authorUsername   the username of the reblogging user
     * @param originalPostId   the ID of the original post
     * @return {@code true} if the user has reblogged the post, {@code false} otherwise
     */
    boolean existsByAuthorUsernameAndOriginalPostId(String authorUsername, Long originalPostId);

    /**
     * Count how many times a given post has been reblogged.
     *
     * @param originalPostId the ID of the original post
     * @return the number of reblogs referencing the given post
     */
    int countByOriginalPostId(Long originalPostId);

    /**
     * Find a specific reblog entry by author and original post.
     *
     * @param authorUsername the username of the reblogging user
     * @param originalPostId the ID of the original post
     * @return an {@link Optional} containing the reblogged {@link Post}, if present
     */
    Optional<Post> findByAuthorUsernameAndOriginalPostId(String authorUsername, Long originalPostId);

    /**
     * Search published posts by matching the query string against title or body,
     * case-insensitive.
     *
     * @param status1        the status to match for title search (typically PUBLISHED)
     * @param titleFragment  substring to search within post titles
     * @param status2        the status to match for body search (typically PUBLISHED)
     * @param bodyFragment   substring to search within post bodies
     * @param pageable       pagination and sorting information
     * @return a {@link Page} of posts whose title or body contains the query string
     */
    Page<Post> findByStatusAndTitleContainingIgnoreCaseOrStatusAndBodyContainingIgnoreCase(
            PostStatus status1, String titleFragment,
            PostStatus status2, String bodyFragment,
            Pageable pageable
    );

    /**
     * Retrieve the IDs of posts sorted by descending number of likes.
     *
     * @param limit pagination information to indicate number of top results
     * @return a {@link List} of post IDs ordered by like count (highest first)
     */
    @Query("""
      SELECT p.id
      FROM Post p
      LEFT JOIN p.likes l
      GROUP BY p.id
      ORDER BY COUNT(l) DESC
      """)
    List<Long> findTopLikedPostIds(Pageable limit);

    /**
     * Retrieve the IDs of posts sorted by descending number of comments.
     *
     * @param limit pagination information to indicate number of top results
     * @return a {@link List} of post IDs ordered by comment count (highest first)
     */
    @Query("""
      SELECT p.id
      FROM Post p
      LEFT JOIN p.comments c
      GROUP BY p.id
      ORDER BY COUNT(c) DESC
      """)
    List<Long> findTopCommentedPostIds(Pageable limit);

    /**
     * Retrieve the IDs of original posts sorted by descending reblog count.
     * Uses a subquery to count reblogs referencing each post.
     *
     * @param limit pagination information to indicate number of top results
     * @return a {@link List} of post IDs ordered by reblog count (highest first)
     */
    @Query("""
      SELECT p.id
      FROM Post p
      ORDER BY (SELECT COUNT(r) FROM Post r WHERE r.originalPost = p) DESC
      """)
    List<Long> findTopRebloggedPostIds(Pageable limit);
}
