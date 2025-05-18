//package com.catalin.vibelog.service.implementations;
//
//import com.catalin.vibelog.model.Media;
//import com.catalin.vibelog.model.enums.MediaType;
//import com.catalin.vibelog.model.Post;
//import com.catalin.vibelog.repository.MediaRepository;
//import com.catalin.vibelog.repository.PostRepository;
//import com.catalin.vibelog.service.MediaService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class MediaServiceImpl implements MediaService {
//
//    private final MediaRepository mediaRepository;
//    private final PostRepository postRepository;
//
//    public MediaServiceImpl(MediaRepository mediaRepository, PostRepository postRepository) {
//        this.mediaRepository = mediaRepository;
//        this.postRepository = postRepository;
//    }
//
//    @Override
//    public Media uploadMedia(Media media) {
//        return mediaRepository.save(media);
//    }
//
//    @Override
//    public Media getMediaById(Long id) {
//        return mediaRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Media not found"));
//    }
//
//    @Override
//    public List<Media> getAllMedia() {
//        return mediaRepository.findAll();
//    }
//
//    @Override
//    public List<Media> getMediaByType(MediaType type) {
//        return mediaRepository.findMediaByType(type);
//    }
//
//    @Override
//    public List<Media> getMediaByFormat(String format) {
//        return mediaRepository.findMediaByFormat(format);
//    }
//
//    @Override
//    public Media getMediaByUrl(String url) {
//        return mediaRepository.findMediaByUrl(url);
//    }
//
//    @Override
//    public void deleteMedia(Long id) {
//        if (!mediaRepository.existsById(id)) {
//            throw new RuntimeException("Media not found for deletion");
//        }
//        mediaRepository.deleteById(id);
//    }
//
//    @Override
//    public List<Media> getMediaForPost(Long postId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new RuntimeException("Post not found"));
//        return post.getMediaList();
//    }
//}
