package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.CommentRequest;
import com.catalin.vibelog.dto.response.CommentResponse;
import com.catalin.vibelog.exception.CommentNotFoundException;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UnauthorizedActionException;
import com.catalin.vibelog.model.Comment;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.CommentService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link CommentService}, using Spring Data JPA.
 * <p>
 * Read-only methods are marked {@code readOnly=true}. Mutating methods
 * are wrapped in transactions for atomicity and rollback on errors.
 * </p>
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository    postRepo;
    private final UserRepository    userRepo;

    public CommentServiceImpl(CommentRepository commentRepo,
                              PostRepository postRepo,
                              UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.postRepo    = postRepo;
        this.userRepo    = userRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
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
        return toDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long postId) {
        // ensure post exists
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
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest req, String authorUsername) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getAuthor().getUsername().equals(authorUsername)) {
            throw new UnauthorizedActionException(
                    "User '" + authorUsername + "' is not the author of comment " + commentId);
        }
        comment.setContent(req.content());
        Comment updated = commentRepo.save(comment);
        return toDto(updated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
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
     * Map a {@link Comment} entity to its {@link CommentResponse} DTO.
     *
     * @param c the comment entity
     * @return the DTO with id, content, authorUsername, and creation time
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
