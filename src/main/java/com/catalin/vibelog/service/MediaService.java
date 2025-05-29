package com.catalin.vibelog.service;

import com.catalin.vibelog.model.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    /** Uploads a file and attaches it to the given post. */
    Media uploadToPost(Long postId, MultipartFile file);

    /** Deletes a single media item (and its file). */
    void deleteMedia(Long mediaId);

    /** Deletes all media attached to a post. */
    void deleteAllForPost(Long postId);

    /** Lists all media items for a post. */
    List<Media> listForPost(Long postId);
}
