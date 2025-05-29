package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Media;
import com.catalin.vibelog.model.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Find all media records belonging to a given post.
     */
    List<Media> findByPostId(Long postId);

    /**
     * Find all media of a certain type (e.g. all videos or all images).
     */
    List<Media> findByType(MediaType type);

    /**
     * (Optional) Delete all media attached to a given post in one shot.
     */
    void deleteByPostId(Long postId);
}
