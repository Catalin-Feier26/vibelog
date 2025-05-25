package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.model.enums.PostStatus;
import com.catalin.vibelog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Posts.
 * <p>
 * Exposes endpoints to create, read, update, delete, and list posts.
 * All mutating operations require an authenticated user.
 * </p>
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Create a new post.
     *
     * @param auth the Spring Security {@link Authentication} holding the current principal
     * @param req  the {@link PostRequest} with title, body, and status
     * @return the created {@link PostResponse}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(
            Authentication auth,
            @Valid @RequestBody PostRequest req
    ) {
        String username = auth.getName();
        return postService.createPost(req, username);
    }

    /**
     * Fetch a single post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return the {@link PostResponse}
     */
    @GetMapping("/{postId}")
    public PostResponse getPost(
            @PathVariable Long postId
    ) {
        return postService.getPostById(postId);
    }

    /**
     * List posts by status (e.g. PUBLISHED or DRAFT) or, if no status is provided,
     * return all published posts.
     *
     * @param status   optional {@link PostStatus} query parameter
     * @param pageable pagination and sorting information
     * @return a page of {@link PostResponse}
     */
    @GetMapping
    public Page<PostResponse> listPosts(
            @RequestParam(required = false, defaultValue = "PUBLISHED") PostStatus status,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC,
                    size = 15
            )
            Pageable pageable
    ) {
        return postService.listPosts(status, pageable);
    }

    /**
     * List posts authored by the current user, newest first.
     *
     * @param auth     the Spring Security {@link Authentication} holding the current principal
     * @param pageable pagination and sorting information
     * @return a page of {@link PostResponse}
     */
    @GetMapping("/me")
    public Page<PostResponse> listMyPosts(
            Authentication auth,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC,
                    size = 15
            )
            Pageable pageable
    ) {
        String username = auth.getName();
        return postService.listPostsByAuthor(username, pageable);
    }

    /**
     * Update an existing post. Only the author may perform this.
     *
     * @param auth    the Spring Security {@link Authentication} holding the current principal
     * @param postId  the ID of the post to update
     * @param req     the {@link PostRequest} with new title/body/status
     * @return the updated {@link PostResponse}
     */
    @PutMapping("/{postId}")
    public PostResponse updatePost(
            Authentication auth,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest req
    ) {
        String username = auth.getName();
        return postService.updatePost(postId, req, username);
    }

    /**
     * Delete a post. Only the author may perform this.
     *
     * @param auth   the Spring Security {@link Authentication} holding the current principal
     * @param postId the ID of the post to delete
     */
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            Authentication auth,
            @PathVariable Long postId
    ) {
        String username = auth.getName();
        postService.deletePost(postId, username);
    }

    /** List the current user’s drafts, newest first */
    @GetMapping("/me/drafts")
    public Page<PostResponse> listMyDrafts(
            Authentication auth,
            @PageableDefault(sort="createdAt", direction=Sort.Direction.DESC)
            Pageable pageable
    ) {
        String username = auth.getName();
        return postService.listPostsByAuthorAndStatus(username, PostStatus.DRAFT, pageable);
    }
}
