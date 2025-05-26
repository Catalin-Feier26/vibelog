// src/main/java/com/catalin/vibelog/service/implementations/SearchServiceImpl.java
package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.response.UserResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.service.SearchService;
import com.catalin.vibelog.model.enums.PostStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    private final UserRepository    userRepo;
    private final PostRepository    postRepo;
    private final LikeRepository    likeRepo;
    private final CommentRepository commentRepo;

    @Autowired
    public SearchServiceImpl(
            UserRepository userRepo,
            PostRepository postRepo,
            LikeRepository likeRepo,
            CommentRepository commentRepo
    ) {
        this.userRepo    = userRepo;
        this.postRepo    = postRepo;
        this.likeRepo    = likeRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    public Page<UserResponseDTO> searchUsers(String q, Pageable pageable) {
        return userRepo
                .findByUsernameContainingIgnoreCase(q, pageable)
                .map(this::toUserDto);
    }

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

    private UserResponseDTO toUserDto(User u) {
        return new UserResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getBio(),
                u.getProfilePicture()
        );
    }

    private PostResponse toPostDto(Post post) {
        int likeCount = likeRepo.countByIdPostId(post.getId());
        int commentCount = commentRepo
                .findAllByPostId(post.getId(), Sort.unsorted())
                .size();

        Long  originalId   = post.getOriginalPost() == null
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
                originalAuthor
        );
    }
}
