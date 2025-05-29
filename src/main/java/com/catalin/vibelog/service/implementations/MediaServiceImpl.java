package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.model.Media;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.enums.MediaType;
import com.catalin.vibelog.repository.MediaRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.service.MediaService;
import com.catalin.vibelog.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Default implementation of {@link MediaService}, handling upload, retrieval,
 * and deletion of media attachments for posts. Integrates file storage via {@link StorageService}
 * and ensures database and storage consistency within transactions.
 */
@Service
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepo;
    private final PostRepository postRepo;
    private final StorageService storageService;

    /**
     * Constructs the MediaService implementation with required dependencies.
     *
     * @param mediaRepo      repository for persisting Media entities
     * @param postRepo       repository for retrieving related Post entities
     * @param storageService service for storing and deleting underlying files
     */
    public MediaServiceImpl(MediaRepository mediaRepo,
                            PostRepository postRepo,
                            StorageService storageService) {
        this.mediaRepo = mediaRepo;
        this.postRepo = postRepo;
        this.storageService = storageService;
    }

    /**
     * Upload a file and attach it to the specified post.
     * The file is stored using {@code storageService} and a {@link Media} entity is saved.
     * In case of persistence failure, the stored file is cleaned up.
     *
     * @param postId the ID of the post to attach the media to
     * @param file   the multipart file to upload
     * @return the persisted {@link Media} entity
     * @throws EntityNotFoundException if no post exists with the given ID
     * @throws RuntimeException        if saving the Media entity fails (file cleanup attempted)
     */
    @Override
    public Media uploadToPost(Long postId, MultipartFile file) {
        // 1) Fetch the post
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        // 2) Store the file
        String url = storageService.store(file);

        // 3) Create Media entity
        Media media = new Media();
        media.setUrl(url);
        media.setFormat(getExtension(file.getOriginalFilename()));
        media.setSize(file.getSize());
        media.setType(detectType(file));
        media.setPost(post);

        // 4) Keep both sides in sync
        post.getMediaList().add(media);

        // 5) Persist entity (cleanup file on error)
        try {
            return mediaRepo.save(media);
        } catch (RuntimeException e) {
            storageService.delete(url);
            throw e;
        }
    }

    /**
     * Delete a single media item by its ID, removing both the database record and stored file.
     *
     * @param mediaId the ID of the media to delete
     * @throws EntityNotFoundException if no Media exists with the given ID
     */
    @Override
    public void deleteMedia(Long mediaId) {
        Media media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + mediaId));
        mediaRepo.delete(media);
        storageService.delete(media.getUrl());
    }

    /**
     * Delete all media items attached to the specified post, including associated files.
     *
     * @param postId the ID of the post whose media attachments to remove
     */
    @Override
    public void deleteAllForPost(Long postId) {
        List<Media> all = mediaRepo.findByPostId(postId);
        mediaRepo.deleteByPostId(postId);
        all.forEach(m -> storageService.delete(m.getUrl()));
    }

    /**
     * List all media items attached to the specified post.
     *
     * @param postId the ID of the post whose media to list
     * @return a list of {@link Media} entities for the post
     */
    @Override
    public List<Media> listForPost(Long postId) {
        return mediaRepo.findByPostId(postId);
    }

    /**
     * Extracts the file extension (without the dot) from a filename,
     * or returns an empty string if none.
     *
     * @param filename the original filename
     * @return the lowercase extension without the leading dot, or empty string
     */
    private String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * Determine {@link MediaType} based on file extension or MIME type fallback.
     * Supports common image and video extensions.
     *
     * @param file the multipart file to inspect
     * @return {@code IMG} for images, {@code VIDEO} for videos
     */
    private MediaType detectType(MultipartFile file) {
        String ext = getExtension(file.getOriginalFilename());
        switch (ext) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp":
                return MediaType.IMG;
            case "mp4", "mov", "avi", "mkv", "webm", "flv":
                return MediaType.VIDEO;
            default:
                String ct = file.getContentType();
                if (ct != null) {
                    if (ct.startsWith("image/")) {
                        return MediaType.IMG;
                    } else if (ct.startsWith("video/")) {
                        return MediaType.VIDEO;
                    }
                }
                return MediaType.IMG;
        }
    }
}
