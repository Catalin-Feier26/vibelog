package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Like;
import com.catalin.vibelog.model.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Like} entities, keyed by composite {@link LikeId} (userId + postId).
 * Supports counting, existence checks, and deletion of likes.
 */
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    /**
     * Count how many likes a particular post has received.
     *
     * @param postId the ID of the post
     * @return the total number of likes for that post
     */
    int countByIdPostId(Long postId);

    /**
     * Check if a specific user has already liked a given post.
     *
     * @param id composite key containing {@code userId} and {@code postId}
     * @return {@code true} if a Like with that composite key exists
     */
    boolean existsById(LikeId id);

    /**
     * Remove a like (i.e., “unlike”) identified by its composite key.
     *
     * @param id composite key containing {@code userId} and {@code postId}
     */
    void deleteById(LikeId id);
}
