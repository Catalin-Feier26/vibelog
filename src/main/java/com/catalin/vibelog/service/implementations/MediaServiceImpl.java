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

@Service
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepo;
    private final PostRepository postRepo;
    private final StorageService storageService;

    public MediaServiceImpl(MediaRepository mediaRepo,
                            PostRepository postRepo,
                            StorageService storageService) {
        this.mediaRepo = mediaRepo;
        this.postRepo = postRepo;
        this.storageService = storageService;
    }

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

    @Override
    public void deleteMedia(Long mediaId) {
        Media media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + mediaId));
        mediaRepo.delete(media);
        storageService.delete(media.getUrl());
    }

    @Override
    public void deleteAllForPost(Long postId) {
        List<Media> all = mediaRepo.findByPostId(postId);
        mediaRepo.deleteByPostId(postId);
        all.forEach(m -> storageService.delete(m.getUrl()));
    }

    @Override
    public List<Media> listForPost(Long postId) {
        return mediaRepo.findByPostId(postId);
    }

    /**
     * Extracts the file extension (without the dot), or returns empty string if none.
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
     * Determines IMG vs VIDEO based on extension first, then MIME type as fallback.
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
