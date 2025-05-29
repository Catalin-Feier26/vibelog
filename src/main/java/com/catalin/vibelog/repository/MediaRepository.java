package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Media;
import com.catalin.vibelog.model.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for managing {@link Media} entities.
 * <p>
 * Provides methods to query and delete media attachments by post or type.
 * </p>
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Retrieve all media attachments for the specified post.
     *
     * @param postId the ID of the post whose media to retrieve
     * @return a {@link List} of {@link Media} entities associated with the post
     */
    List<Media> findByPostId(Long postId);

    /**
     * Retrieve all media records of the given {@link MediaType}.
     *
     * @param type the {@link MediaType} to filter by (e.g., IMG or VIDEO)
     * @return a {@link List} of {@link Media} entities matching the type
     */
    List<Media> findByType(MediaType type);

    /**
     * Delete all media attachments associated with the specified post.
     *
     * @param postId the ID of the post whose media should be removed
     */
    void deleteByPostId(Long postId);
}