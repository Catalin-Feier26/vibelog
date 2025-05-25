package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.response.LikeResponse;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.model.Like;
import com.catalin.vibelog.model.LikeId;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Default implementation of {@link LikeService}, using Spring Data JPA.
 * <p>
 * All methods are transactional to ensure consistency of like toggling.
 * </p>
 */
@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public LikeServiceImpl(LikeRepository likeRepo,
                           PostRepository postRepo,
                           UserRepository userRepo) {
        this.likeRepo = likeRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public LikeResponse toggleLike(Long postId, String username) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));

        LikeId id = new LikeId(user.getId(), post.getId());
        boolean liked;
        if (likeRepo.existsById(id)) {
            likeRepo.deleteById(id);
            liked = false;
        } else {
            Like like = new Like();
            like.setId(id);
            like.setUser(user);
            like.setPost(post);
            like.setLikedAt(LocalDateTime.now());
            likeRepo.save(like);
            liked = true;
        }

        int total = likeRepo.countByIdPostId(postId);
        return new LikeResponse(postId, liked, total);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public int countLikes(Long postId) {
        return likeRepo.countByIdPostId(postId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));
        LikeId id = new LikeId(user.getId(), postId);
        return likeRepo.existsById(id);
    }
}
