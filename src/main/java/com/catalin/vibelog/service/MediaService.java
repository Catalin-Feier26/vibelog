package com.catalin.vibelog.service;

import com.catalin.vibelog.model.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Defines operations for uploading, retrieving, and deleting media attachments associated with posts.
 * Implementations handle persisting file data, linking to posts, and cleaning up storage.
 */
public interface MediaService {

    /**
     * Upload a media file and attach it to the specified post.
     *
     * @param postId the ID of the post to attach the media to
     * @param file   the {@link MultipartFile} to upload
     * @return the persisted {@link Media} entity representing the uploaded file
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no post exists with the given ID
     */
    Media uploadToPost(Long postId, MultipartFile file);

    /**
     * Delete a single media item by its ID, including removing the underlying file.
     *
     * @param mediaId the ID of the media item to delete
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no media exists with the given ID
     */
    void deleteMedia(Long mediaId);

    /**
     * Delete all media items attached to the specified post, including their files.
     *
     * @param postId the ID of the post whose media should be deleted
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no post exists with the given ID
     */
    void deleteAllForPost(Long postId);

    /**
     * List all media items attached to the specified post.
     *
     * @param postId the ID of the post whose media items to list
     * @return a {@link List} of {@link Media} entities for the post
     * @throws com.catalin.vibelog.exception.ResourceNotFoundException if no post exists with the given ID
     */
    List<Media> listForPost(Long postId);
}
