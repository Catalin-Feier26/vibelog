package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for {@link Comment} entities.
 * Allows CRUD plus listing all comments under a specific post.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * List all comments belonging to a given post.
     *
     * @param postId the ID of the post whose comments are being retrieved
     * @param sort   sorting order (e.g. Sort.by("createdAt").descending())
     * @return a {@link List} of {@link Comment} for the specified post, in the given order
     */
    List<Comment> findAllByPostId(Long postId, Sort sort);
}
