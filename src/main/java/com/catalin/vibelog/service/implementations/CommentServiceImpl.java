package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.CommentRequest;
import com.catalin.vibelog.dto.response.CommentResponse;
import com.catalin.vibelog.events.CommentEvent;
import com.catalin.vibelog.exception.CommentNotFoundException;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UnauthorizedActionException;
import com.catalin.vibelog.model.Comment;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.ReportRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.CommentService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link CommentService}, handling creation, retrieval,
 * updating, and deletion of comments on posts.
 * Read-only methods are marked {@code readOnly=true}, and mutating methods
 * run within transactions to ensure atomicity and rollback.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final ApplicationEventPublisher publisher;
    private final ReportRepository  reportRepo;
    private final CommentRepository commentRepo;
    private final PostRepository    postRepo;
    private final UserRepository    userRepo;

    /**
     * Constructs the comment service with required repositories and event publisher.
     *
     * @param reportRepo   repository for cleaning up reports on moderation actions
     * @param publisher    event publisher for firing comment events
     * @param commentRepo  repository for comment persistence and queries
     * @param postRepo     repository for validating posts
     * @param userRepo     repository for validating users
     */
    public CommentServiceImpl(ReportRepository  reportRepo,
                              ApplicationEventPublisher publisher,
                              CommentRepository commentRepo,
                              PostRepository postRepo,
                              UserRepository userRepo) {
        this.reportRepo = reportRepo;
        this.publisher = publisher;
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    /**
     * Add a new comment under the specified post authored by the given user.
     * Publishes a {@link CommentEvent} after successful creation.
     *
     * @param postId         the ID of the post to comment on
     * @param req            the {@link CommentRequest} containing comment content
     * @param authorUsername the username of the commenter (authenticated user)
     * @return the created {@link CommentResponse}
     * @throws PostNotFoundException        if no post exists with the given ID
     * @throws IllegalStateException        if the author user cannot be found
     */
    @Override
    public CommentResponse addComment(Long postId, CommentRequest req, String authorUsername) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        User author = userRepo.findByUsername(authorUsername)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + authorUsername));
        Comment comment = new Comment();
        comment.setContent(req.content());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setPost(post);
        Comment saved = commentRepo.save(comment);

        publisher.publishEvent(new CommentEvent(
                this,
                post.getId(),
                saved.getId(),
                author.getUsername(),
                post.getAuthor().getUsername()
        ));

        return toDto(saved);
    }

    /**
     * List all comments for the given post, ordered by creation time ascending.
     *
     * @param postId the ID of the post whose comments to retrieve
     * @return a list of {@link CommentResponse} DTOs
     * @throws PostNotFoundException if no post exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long postId) {
        if (!postRepo.existsById(postId)) {
            throw new PostNotFoundException(postId);
        }
        return commentRepo
                .findAllByPostId(postId, Sort.by("createdAt").ascending())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing comment's content. Only the original author may update.
     *
     * @param commentId      the ID of the comment to update
     * @param req            the {@link CommentRequest} containing new content
     * @param authorUsername the username of the user attempting the update
     * @return the updated {@link CommentResponse}
     * @throws CommentNotFoundException      if no comment exists with the given ID
     * @throws UnauthorizedActionException   if the user is not the comment's author
     */
    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest req, String authorUsername) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getAuthor().getUsername().equals(authorUsername)) {
            throw new UnauthorizedActionException(
                    "User '" + authorUsername + "' is not the author of comment " + commentId);
        }
        comment.setContent(req.content());
        comment.setEditedAt(LocalDateTime.now());
        Comment updated = commentRepo.save(comment);
        return toDto(updated);
    }

    /**
     * Delete a comment. Only the original author may perform this action.
     *
     * @param commentId      the ID of the comment to delete
     * @param authorUsername the username of the user attempting deletion
     * @throws CommentNotFoundException      if no comment exists with the given ID
     * @throws UnauthorizedActionException   if the user is not the comment's author
     */
    @Override
    public void deleteComment(Long commentId, String authorUsername) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getAuthor().getUsername().equals(authorUsername)) {
            throw new UnauthorizedActionException(
                    "User '" + authorUsername + "' is not the author of comment " + commentId);
        }
        commentRepo.delete(comment);
    }

    /**
     * Delete a comment as a moderator, deleting associated reports first.
     *
     * @param commentId the ID of the comment to delete
     * @throws CommentNotFoundException if no comment exists with the given ID
     */
    @Override
    public void deleteCommentAsModerator(Long commentId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        reportRepo.deleteAllByCommentId(commentId);
        commentRepo.delete(comment);
    }

    /**
     * Map a {@link Comment} entity to a {@link CommentResponse} DTO.
     *
     * @param c the comment entity to convert
     * @return the populated {@link CommentResponse}
     */
    private CommentResponse toDto(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getContent(),
                c.getAuthor().getUsername(),
                c.getCreatedAt()
        );
    }
}