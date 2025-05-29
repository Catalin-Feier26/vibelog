package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.response.LikeResponse;
import com.catalin.vibelog.events.LikeEvent;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.model.Like;
import com.catalin.vibelog.model.LikeId;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.LikeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Default implementation of {@link LikeService}, handling toggling and querying of likes on posts.
 * <p>
 * All methods are transactional to ensure consistency when creating or removing likes.
 * Publishes a {@link LikeEvent} when a new like is created by someone other than the post's author.
 * </p>
 */
@Service
public class LikeServiceImpl implements LikeService {

    private final ApplicationEventPublisher publisher;
    private final LikeRepository likeRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    /**
     * Constructs the LikeService implementation with necessary repositories and event publisher.
     *
     * @param likeRepo  repository for persisting and querying Like entities
     * @param postRepo  repository for retrieving Post entities
     * @param userRepo  repository for retrieving User entities
     * @param publisher event publisher for firing LikeEvent notifications
     */
    public LikeServiceImpl(LikeRepository likeRepo,
                           PostRepository postRepo,
                           UserRepository userRepo,
                           ApplicationEventPublisher publisher) {
        this.likeRepo = likeRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.publisher = publisher;
    }

    /**
     * Toggle a like for the specified post by the given user.
     * If a like already exists, it will be removed; otherwise, a new like is created.
     * Publishes a {@link LikeEvent} when a new like is created and the liker is not the post's author.
     *
     * @param postId   the ID of the post to like or unlike
     * @param username the username of the acting user
     * @return a {@link LikeResponse} containing post ID, new liked status, and total like count
     * @throws PostNotFoundException     if no post exists with the given ID
     * @throws IllegalStateException     if the authenticated user cannot be found
     */
    @Override
    @Transactional
    public LikeResponse toggleLike(Long postId, String username) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + username));

        LikeId id = new LikeId(user.getId(), post.getId());
        boolean liked;
        if (likeRepo.existsById(id)) {
            // Remove existing like
            likeRepo.deleteById(id);
            liked = false;
        } else {
            // Create new like
            Like like = new Like();
            like.setId(id);
            like.setUser(user);
            like.setPost(post);
            like.setLikedAt(LocalDateTime.now());
            likeRepo.save(like);
            liked = true;
            // Publish event if liker is not the author
            if (liked && !username.equals(post.getAuthor().getUsername())) {
                publisher.publishEvent(new LikeEvent(
                        this,
                        postId,
                        username,
                        post.getAuthor().getUsername()
                ));
            }
        }

        int total = likeRepo.countByIdPostId(postId);
        return new LikeResponse(postId, liked, total);
    }

    /**
     * Count the total number of likes for a given post.
     *
     * @param postId the ID of the post to count likes for
     * @return the number of likes
     */
    @Override
    @Transactional(readOnly = true)
    public int countLikes(Long postId) {
        return likeRepo.countByIdPostId(postId);
    }

    /**
     * Check whether the specified user has liked a given post.
     *
     * @param postId   the ID of the post to check
     * @param username the username of the user to check for
     * @return {@code true} if the user has liked the post, {@code false} otherwise
     * @throws IllegalStateException if the authenticated user cannot be found
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + username));
        LikeId id = new LikeId(user.getId(), postId);
        return likeRepo.existsById(id);
    }
}
