package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.MediaResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.model.Media;
import com.catalin.vibelog.model.enums.PostStatus;

import com.catalin.vibelog.service.MediaService;
import com.catalin.vibelog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    private final PostService  postService;
    private final MediaService mediaService;

    public PostController(PostService postService,
                          MediaService mediaService) {
        this.postService  = postService;
        this.mediaService = mediaService;
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

    /**
     * List all **published** posts by the given username, newest first.
     *
     * @param username the username whose posts to fetch
     * @param pageable pagination & sorting (default: createdAt desc, size=15)
     * @return a page of {@link PostResponse}
     */
    @GetMapping("/user/{username}")
    public Page<PostResponse> listPublishedPostsByUser(
            @PathVariable String username,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC,
                    size = 15
            )
            Pageable pageable
    ) {
        return postService.listPostsByAuthorAndStatus(
                username,
                PostStatus.PUBLISHED,
                pageable
        );
    }
    /**
     * Reblog an existing post: creates a new Post with originalPost set.
     *
     * @param postId the ID of the post to reblog
     * @param auth   the authenticated principal
     * @return the newly created PostResponse (status PUBLISHED)
     */
    @PostMapping("/{postId}/reblog")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse reblogPost(
            @PathVariable Long postId,
            Authentication auth
    ) {
        String username = auth.getName();
        PostResponse original = postService.getPostById(postId);
        PostRequest req = new PostRequest(
                original.title(),
                original.body(),
                PostStatus.PUBLISHED.name(),
                postId
        );
        return postService.createPost(req, username);
    }
    /**
     * Undo a reblog by deleting the reblog‐post authored by the current user.
     */
    @DeleteMapping("/{postId}/reblog")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoReblog(
            @PathVariable Long postId,
            Authentication auth
    ) {
        postService.undoReblog(auth.getName(), postId);
    }

    /**
     * Check if the current user has reblogged this post.
     *
     * @return { "reblogged": true|false }
     */
    @GetMapping("/{postId}/reblog/state")
    public Map<String, Boolean> reblogState(
            @PathVariable Long postId,
            Authentication auth
    ) {
        boolean state = postService.isReblogged(auth.getName(), postId);
        return Map.of("reblogged", state);
    }

    /**
     * Count how many times a post has been reblogged.
     *
     * @return { "count": 123 }
     */
    @GetMapping("/{postId}/reblog/count")
    public Map<String, Integer> reblogCount(@PathVariable Long postId) {
        int count = postService.countReblogs(postId);
        return Map.of("count", count);
    }

    /**
     * POST /api/posts/{postId}/media : attach a file to a post
     */
    @PostMapping("/{postId}/media")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponseDTO uploadMedia(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file
    ) {
        Media m = mediaService.uploadToPost(postId, file);
        return new MediaResponseDTO(m.getId(), m.getUrl(), m.getType().name());
    }

    /**
     * GET /api/posts/{postId}/media : list all attachments for a post
     */
    @GetMapping("/{postId}/media")
    public List<MediaResponseDTO> listMedia(@PathVariable Long postId) {
        return mediaService.listForPost(postId).stream()
                .map(m -> new MediaResponseDTO(m.getId(), m.getUrl(), m.getType().name()))
                .toList();
    }

    /**
     * DELETE /api/posts/media/{mediaId} : remove a single attachment
     */
    @DeleteMapping("/media/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedia(@PathVariable Long mediaId) {
        mediaService.deleteMedia(mediaId);
    }
}
