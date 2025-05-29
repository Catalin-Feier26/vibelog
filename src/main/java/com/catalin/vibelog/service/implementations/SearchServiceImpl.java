package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.response.MediaResponseDTO;
import com.catalin.vibelog.dto.response.UserResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.PostStatus;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.service.MediaService;
import com.catalin.vibelog.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link SearchService}, providing search capabilities
 * for users and posts using repository queries and mapping to DTOs.
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final LikeRepository likeRepo;
    private final CommentRepository commentRepo;
    private final MediaService mediaService;

    /**
     * Constructs the service with required repository and media dependencies.
     *
     * @param userRepo      repository for user lookups
     * @param postRepo      repository for post lookups
     * @param likeRepo      repository for like counts
     * @param commentRepo   repository for comment retrieval
     * @param mediaServiceImpl service for retrieving media attachments
     */
    @Autowired
    public SearchServiceImpl(
            UserRepository userRepo,
            PostRepository postRepo,
            LikeRepository likeRepo,
            CommentRepository commentRepo,
            MediaService mediaServiceImpl) {
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.likeRepo = likeRepo;
        this.commentRepo = commentRepo;
        this.mediaService = mediaServiceImpl;
    }

    /**
     * Search users by username fragment, case-insensitive, with pagination.
     *
     * @param q        the search query string to match against usernames
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link UserResponseDTO} matching the query
     */
    @Override
    public Page<UserResponseDTO> searchUsers(String q, Pageable pageable) {
        return userRepo
                .findByUsernameContainingIgnoreCase(q, pageable)
                .map(this::toUserDto);
    }

    /**
     * Search published posts by title or body containing the query string,
     * case-insensitive, with pagination.
     *
     * @param q        the search query string to match against title or body
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link PostResponse} matching the query
     */
    @Override
    public Page<PostResponse> searchPosts(String q, Pageable pageable) {
        return postRepo
                .findByStatusAndTitleContainingIgnoreCaseOrStatusAndBodyContainingIgnoreCase(
                        PostStatus.PUBLISHED, q,
                        PostStatus.PUBLISHED, q,
                        pageable
                )
                .map(this::toPostDto);
    }

    /**
     * Map a {@link User} entity to a {@link UserResponseDTO}.
     *
     * @param u the user entity to convert
     * @return the corresponding {@link UserResponseDTO}
     */
    private UserResponseDTO toUserDto(User u) {
        return new UserResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getBio(),
                u.getProfilePicture()
        );
    }

    /**
     * Map a {@link Post} entity to a {@link PostResponse}, including
     * counts for likes and comments, original post details, and media attachments.
     *
     * @param post the post entity to convert
     * @return the corresponding {@link PostResponse}
     */
    private PostResponse toPostDto(Post post) {
        int likeCount = likeRepo.countByIdPostId(post.getId());
        int commentCount = commentRepo
                .findAllByPostId(post.getId(), Sort.unsorted())
                .size();

        Long originalId = post.getOriginalPost() == null
                ? null
                : post.getOriginalPost().getId();
        String originalAuthor = post.getOriginalPost() == null
                ? null
                : post.getOriginalPost().getAuthor().getUsername();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getStatus().name(),
                post.getCreatedAt(),
                post.getAuthor().getUsername(),
                likeCount,
                commentCount,
                originalId,
                originalAuthor,
                mediaService.listForPost(post.getId()).stream()
                        .map(m -> new MediaResponseDTO(m.getId(), m.getUrl(), m.getType().name()))
                        .toList()
        );
    }
}
