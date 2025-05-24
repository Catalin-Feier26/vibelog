package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UnauthorizedActionException;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.PostStatus;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link PostService}, using Spring Data JPA for persistence.
 * <p>
 * Read-only methods are optimized with {@code readOnly=true}. Mutating methods
 * are wrapped in transactions to ensure atomicity and rollback on failure.
 * </p>
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository    postRepo;
    private final UserRepository    userRepo;
    private final CommentRepository commentRepo;
    private final LikeRepository    likeRepo;

    /**
     * Constructs a new {@code PostServiceImpl} with the required repositories.
     *
     * @param postRepo    the repository managing {@link Post} entities
     * @param userRepo    the repository managing {@link User} entities
     * @param commentRepo the repository managing {@link com.catalin.vibelog.model.Comment} entities
     * @param likeRepo    the repository managing {@link com.catalin.vibelog.model.Like} entities
     */
    public PostServiceImpl(PostRepository postRepo,
                           UserRepository userRepo,
                           CommentRepository commentRepo,
                           LikeRepository likeRepo) {
        this.postRepo    = postRepo;
        this.userRepo    = userRepo;
        this.commentRepo = commentRepo;
        this.likeRepo    = likeRepo;
    }

    /**
     * {@inheritDoc}
     * <p>Creates a new post in the given status, authored by the specified user.</p>
     */
    @Override
    @Transactional
    public PostResponse createPost(PostRequest req, String authorUsername) {
        User author = userRepo.findByUsername(authorUsername)
                .orElseThrow(() -> new PostNotFoundException(null)); // or a UserNotFoundException
        Post post = new Post();
        post.setTitle(req.title());
        post.setBody(req.body());
        post.setStatus(PostStatus.fromString(req.status()));
        post.setAuthor(author);
        Post saved = postRepo.save(post);
        return toDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return toDto(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPosts(PostStatus status, Pageable pageable) {
        return postRepo
                .findAllByStatus(status, pageable)
                .map(this::toDto);
    }

    /**
     * {@inheritDoc}
     * <p>Only the original author may update their post.</p>
     */
    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest req, String authorUsername) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.getAuthor().getUsername().equals(authorUsername)) {
            throw new UnauthorizedActionException(
                    "User '" + authorUsername + "' is not the author of post " + postId);
        }
        post.setTitle(req.title());
        post.setBody(req.body());
        post.setStatus(PostStatus.fromString(req.status()));
        Post updated = postRepo.save(post);
        return toDto(updated);
    }

    /**
     * {@inheritDoc}
     * <p>Only the original author may delete their post.</p>
     */
    @Override
    @Transactional
    public void deletePost(Long postId, String authorUsername) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.getAuthor().getUsername().equals(authorUsername)) {
            throw new UnauthorizedActionException(
                    "User '" + authorUsername + "' is not the author of post " + postId);
        }
        postRepo.delete(post);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPostsByAuthor(String authorUsername, Pageable pageable) {
        // enforce descending order by default if none supplied
        Pageable pageWithDefaultSort =
                pageable.getSort().isSorted()
                        ? pageable
                        : PageRequest.of(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("createdAt").descending());

        return postRepo
                .findByAuthorUsername(authorUsername, pageWithDefaultSort)
                .map(this::toDto);
    }
    /**
     * Maps a {@link Post} entity to a {@link PostResponse} DTO.
     * <p>Also includes counts for likes and comments.</p>
     *
     * @param post the entity to convert
     * @return the populated response DTO
     */
    private PostResponse toDto(Post post) {
        int likeCount    = likeRepo.countByIdPostId(post.getId());
        int commentCount = commentRepo
                .findAllByPostId(post.getId(), Sort.unsorted())
                .size();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getStatus().name(),
                post.getCreatedAt(),
                post.getAuthor().getUsername(),
                likeCount,
                commentCount
        );
    }

}
