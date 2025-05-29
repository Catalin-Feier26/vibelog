package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.MediaResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.events.ReblogEvent;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UnauthorizedActionException;
import com.catalin.vibelog.exception.UserNotFoundException;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.PostStatus;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.ReportRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.MediaService;
import com.catalin.vibelog.service.PostService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Default implementation of {@link PostService}, using Spring Data JPA for persistence.
 * <p>
 * Read-only methods are optimized with <code>readOnly=true</code> and mutating methods
 * are executed within transactions to ensure atomicity and rollback on failure.
 * Events are published for reblogs.
 * </p>
 */
@Service
public class PostServiceImpl implements PostService {

    private final ApplicationEventPublisher publisher;
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;
    private final LikeRepository likeRepo;
    private final ReportRepository reportRepo;
    private final MediaService mediaService;

    /**
     * Constructs the PostService implementation with required dependencies.
     *
     * @param reportRepo    repository for handling report cleanup on moderator actions
     * @param postRepo      repository for post persistence
     * @param userRepo      repository for user lookups
     * @param commentRepo   repository for comment counts
     * @param likeRepo      repository for like counts
     * @param publisher     event publisher for reblog events
     * @param mediaService  service for retrieving media attachments
     */
    public PostServiceImpl(
            ReportRepository reportRepo,
            PostRepository postRepo,
            UserRepository userRepo,
            CommentRepository commentRepo,
            LikeRepository likeRepo,
            ApplicationEventPublisher publisher,
            MediaService mediaService
    ) {
        this.reportRepo = reportRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
        this.publisher = publisher;
        this.mediaService = mediaService;
    }

    /**
     * Create a new post authored by the given user.
     * Publishes a {@link ReblogEvent} if this post is a reblog.
     *
     * @param req             the {@link PostRequest} containing title, body, status, and optional originalPostId
     * @param authorUsername  the username of the author
     * @return the created {@link PostResponse}
     * @throws PostNotFoundException       if the author or original post (when provided) does not exist
     */
    @Override
    @Transactional
    public PostResponse createPost(PostRequest req, String authorUsername) {
        User author = userRepo.findByUsername(authorUsername)
                .orElseThrow(() -> new PostNotFoundException(null));
        Post post = new Post();
        post.setTitle(req.title());
        post.setBody(req.body());
        post.setStatus(PostStatus.fromString(req.status()));
        post.setAuthor(author);

        if (req.originalPostId() != null) {
            Post original = postRepo.findById(req.originalPostId())
                    .orElseThrow(() -> new PostNotFoundException(req.originalPostId()));
            post.setOriginalPost(original);
            publisher.publishEvent(new ReblogEvent(
                    this,
                    original.getId(),
                    authorUsername,
                    original.getAuthor().getUsername()
            ));
        }

        Post saved = postRepo.save(post);
        return toDto(saved);
    }

    /**
     * Retrieve a post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return the corresponding {@link PostResponse}
     * @throws PostNotFoundException if no post exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return toDto(post);
    }

    /**
     * List posts by status with pagination.
     *
     * @param status   the {@link PostStatus} filter
     * @param pageable pagination and sorting information
     * @return a page of {@link PostResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPosts(PostStatus status, Pageable pageable) {
        return postRepo
                .findAllByStatus(status, pageable)
                .map(this::toDto);
    }

    /**
     * Update a post's content and status. Only the original author may update.
     *
     * @param postId          the ID of the post to update
     * @param req             the {@link PostRequest} with new title, body, and status
     * @param authorUsername  the username of the user attempting the update
     * @return the updated {@link PostResponse}
     * @throws PostNotFoundException        if no post exists with the given ID
     * @throws UnauthorizedActionException  if the user is not the original author
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
        post.setUpdatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.fromString(req.status()));
        Post updated = postRepo.save(post);
        return toDto(updated);
    }

    /**
     * Delete a post. Only the original author may delete their post.
     *
     * @param postId         the ID of the post to delete
     * @param authorUsername the username of the user attempting deletion
     * @throws PostNotFoundException       if no post exists with the given ID
     * @throws UnauthorizedActionException if the user is not the original author
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

    /**
     * List all posts by a specific author, with default sort by newest if none provided.
     *
     * @param authorUsername the username of the author
     * @param pageable       pagination and sorting information
     * @return a page of {@link PostResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPostsByAuthor(String authorUsername, Pageable pageable) {
        Pageable pageWithDefaultSort = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        return postRepo
                .findByAuthorUsername(authorUsername, pageWithDefaultSort)
                .map(this::toDto);
    }

    /**
     * List all posts by author filtered by status.
     *
     * @param username the author's username
     * @param status   the {@link PostStatus} filter
     * @param pageable pagination and sorting information
     * @return a page of {@link PostResponse}
     * @throws UserNotFoundException if the author does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPostsByAuthorAndStatus(String username, PostStatus status, Pageable pageable) {
        userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return postRepo.findByAuthorUsernameAndStatus(username, status, pageable)
                .map(this::toDto);
    }

    /**
     * Convert a {@link Post} entity to a {@link PostResponse} DTO,
     * including counts for likes, comments, original post info, and media attachments.
     *
     * @param post the post entity to convert
     * @return the corresponding response DTO
     */
    private PostResponse toDto(Post post) {
        int likeCount = likeRepo.countByIdPostId(post.getId());
        int commentCount = commentRepo.findAllByPostId(post.getId(), Sort.unsorted()).size();
        Long originalPostId = post.getOriginalPost() == null
                ? null
                : post.getOriginalPost().getId();
        String originalAuthor = post.getOriginalPost() == null
                ? null
                : post.getOriginalPost().getAuthor().getUsername();
        List<MediaResponseDTO> mediaDtos = mediaService.listForPost(post.getId()).stream()
                .map(m -> new MediaResponseDTO(m.getId(), m.getUrl(), m.getType().name()))
                .toList();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getStatus().name(),
                post.getCreatedAt(),
                post.getAuthor().getUsername(),
                likeCount,
                commentCount,
                originalPostId,
                originalAuthor,
                mediaDtos
        );
    }

    /**
     * Remove a reblog by the given user for the specified original post.
     *
     * @param username       the username of the reblogger
     * @param originalPostId the ID of the original post to undo reblog
     * @throws PostNotFoundException if no matching reblog exists
     */
    @Override
    @Transactional
    public void undoReblog(String username, Long originalPostId) {
        Post reblog = postRepo.findByAuthorUsernameAndOriginalPostId(username, originalPostId)
                .orElseThrow(() -> new PostNotFoundException(originalPostId));
        postRepo.delete(reblog);
    }

    /**
     * Check if a user has reblogged a specific post.
     *
     * @param username       the username to check
     * @param originalPostId the ID of the original post
     * @return {@code true} if the user has reblogged, {@code false} otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isReblogged(String username, Long originalPostId) {
        return postRepo.existsByAuthorUsernameAndOriginalPostId(username, originalPostId);
    }

    /**
     * Count how many times a post has been reblogged.
     *
     * @param originalPostId the ID of the original post
     * @return the number of reblogs
     */
    @Override
    @Transactional(readOnly = true)
    public int countReblogs(Long originalPostId) {
        return postRepo.countByOriginalPostId(originalPostId);
    }

    /**
     * Delete a post as a moderator, including cleaning up associated reports.
     *
     * @param postId the ID of the post to delete
     * @throws PostNotFoundException if no post exists with the given ID
     */
    @Override
    @Transactional
    public void deletePostAsModerator(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        reportRepo.deleteAllByPostId(postId);
        postRepo.delete(post);
    }
}
