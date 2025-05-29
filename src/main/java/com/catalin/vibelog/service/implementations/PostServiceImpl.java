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
 * Read-only methods are optimized with {@code readOnly=true}. Mutating methods
 * are wrapped in transactions to ensure atomicity and rollback on failure.
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

    public PostServiceImpl(
            ReportRepository reportRepo,
            PostRepository postRepo,
            UserRepository userRepo,
            CommentRepository commentRepo,
            LikeRepository likeRepo,
            ApplicationEventPublisher publisher,
            MediaService mediaService
    ) {
        this.publisher = publisher;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.commentRepo = commentRepo;
        this.likeRepo = likeRepo;
        this.reportRepo = reportRepo;
        this.mediaService = mediaService;
    }

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

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return toDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPosts(PostStatus status, Pageable pageable) {
        return postRepo
                .findAllByStatus(status, pageable)
                .map(this::toDto);
    }

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
        Pageable pageWithDefaultSort = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        return postRepo
                .findByAuthorUsername(authorUsername, pageWithDefaultSort)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPostsByAuthorAndStatus(String username, PostStatus status, Pageable pageable) {
        userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return postRepo.findByAuthorUsernameAndStatus(username, status, pageable)
                .map(this::toDto);
    }

    private PostResponse toDto(Post post) {
        int likeCount = likeRepo.countByIdPostId(post.getId());
        int commentCount = commentRepo.findAllByPostId(post.getId(), Sort.unsorted()).size();

        // null-safe original post info
        Long originalPostId = post.getOriginalPost() == null
                ? null
                : post.getOriginalPost().getId();
        String originalAuthor = post.getOriginalPost() == null
                ? null
                : post.getOriginalPost().getAuthor().getUsername();

        // media attachments
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

    @Override
    @Transactional
    public void undoReblog(String username, Long originalPostId) {
        Post reblog = postRepo.findByAuthorUsernameAndOriginalPostId(username, originalPostId)
                .orElseThrow(() -> new PostNotFoundException(originalPostId));
        postRepo.delete(reblog);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isReblogged(String username, Long originalPostId) {
        return postRepo.existsByAuthorUsernameAndOriginalPostId(username, originalPostId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countReblogs(Long originalPostId) {
        return postRepo.countByOriginalPostId(originalPostId);
    }

    @Override
    @Transactional
    public void deletePostAsModerator(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        reportRepo.deleteAllByPostId(postId);
        postRepo.delete(post);
    }
}
