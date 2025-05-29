package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.MediaResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
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
 * REST controller for managing posts.
 * <p>
 * Exposes endpoints to create, retrieve, update, delete, list, and manage media
 * and reblogs for posts. All mutating operations require the user to be authenticated.
 * </p>
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MediaService mediaService;

    /**
     * Constructs the PostController with required services.
     *
     * @param postService  service handling post business logic
     * @param mediaService service handling media attachments
     */
    public PostController(PostService postService,
                          MediaService mediaService) {
        this.postService = postService;
        this.mediaService = mediaService;
    }

    /**
     * Create a new post.
     *
     * @param auth the Spring Security {@link Authentication} containing the current principal's username
     * @param req  the {@link PostRequest} DTO with title, body, status, and optional originalPostId
     * @return the created {@link PostResponse}
     * @throws com.catalin.vibelog.exception.PostNotFoundException if the originalPostId does not exist
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
     * @return the {@link PostResponse} DTO
     * @throws com.catalin.vibelog.exception.PostNotFoundException if no post exists with the given ID
     */
    @GetMapping("/{postId}")
    public PostResponse getPost(
            @PathVariable Long postId
    ) {
        return postService.getPostById(postId);
    }

    /**
     * List posts by status (published or draft).
     *
     * @param status   optional {@link com.catalin.vibelog.model.enums.PostStatus}; defaults to PUBLISHED
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link PostResponse} matching the status
     */
    @GetMapping
    public Page<PostResponse> listPosts(
            @RequestParam(required = false, defaultValue = "PUBLISHED") com.catalin.vibelog.model.enums.PostStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 15)
            Pageable pageable
    ) {
        return postService.listPosts(status, pageable);
    }

    /**
     * List posts authored by the current user, newest first.
     *
     * @param auth     the Spring Security {@link Authentication} containing the current user's username
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link PostResponse}
     */
    @GetMapping("/me")
    public Page<PostResponse> listMyPosts(
            Authentication auth,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 15)
            Pageable pageable
    ) {
        String username = auth.getName();
        return postService.listPostsByAuthor(username, pageable);
    }

    /**
     * Update an existing post. Only the original author may update.
     *
     * @param auth    the Spring Security {@link Authentication} containing the current user's username
     * @param postId  the ID of the post to update
     * @param req     the {@link PostRequest} DTO with updated title, body, and status
     * @return the updated {@link PostResponse}
     * @throws com.catalin.vibelog.exception.PostNotFoundException        if the post does not exist
     * @throws com.catalin.vibelog.exception.UnauthorizedActionException if the user is not the author
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
     * Delete a post. Only the original author may delete.
     *
     * @param auth   the Spring Security {@link Authentication} containing the current user's username
     * @param postId the ID of the post to delete
     * @throws com.catalin.vibelog.exception.PostNotFoundException        if the post does not exist
     * @throws com.catalin.vibelog.exception.UnauthorizedActionException if the user is not the author
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

    /**
     * List the current user's drafts, newest first.
     *
     * @param auth     the Spring Security {@link Authentication} containing the current user's username
     * @param pageable pagination and sorting information
     * @return a {@link Page} of draft {@link PostResponse}
     */
    @GetMapping("/me/drafts")
    public Page<PostResponse> listMyDrafts(
            Authentication auth,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        String username = auth.getName();
        return postService.listPostsByAuthorAndStatus(username, com.catalin.vibelog.model.enums.PostStatus.DRAFT, pageable);
    }

    /**
     * List all published posts by a specific user, newest first.
     *
     * @param username the username whose published posts to fetch
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link PostResponse}
     */
    @GetMapping("/user/{username}")
    public Page<PostResponse> listPublishedPostsByUser(
            @PathVariable String username,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 15)
            Pageable pageable
    ) {
        return postService.listPostsByAuthorAndStatus(username, com.catalin.vibelog.model.enums.PostStatus.PUBLISHED, pageable);
    }

    /**
     * Reblog an existing post by creating a new published post linked to the original.
     *
     * @param postId the ID of the post to reblog
     * @param auth   the Spring Security {@link Authentication} containing the current user's username
     * @return the newly created {@link PostResponse}
     * @throws com.catalin.vibelog.exception.PostNotFoundException if the original post does not exist
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
                com.catalin.vibelog.model.enums.PostStatus.PUBLISHED.name(),
                postId
        );
        return postService.createPost(req, username);
    }

    /**
     * Undo a reblog by deleting the reblog post created by the current user.
     *
     * @param postId the ID of the original post whose reblog to undo
     * @param auth   the Spring Security {@link Authentication} containing the current user's username
     * @throws com.catalin.vibelog.exception.PostNotFoundException if no such reblog exists
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
     * Check if the current user has reblogged a specific post.
     *
     * @param postId the ID of the original post
     * @param auth   the Spring Security {@link Authentication} containing the current user's username
     * @return a map with key "reblogged" and value true if reblogged, false otherwise
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
     * @param postId the ID of the original post
     * @return a map with key "count" and the number of reblogs
     */
    @GetMapping("/{postId}/reblog/count")
    public Map<String, Integer> reblogCount(@PathVariable Long postId) {
        int count = postService.countReblogs(postId);
        return Map.of("count", count);
    }

    /**
     * Attach a media file to a post.
     *
     * @param postId the ID of the post to attach media to
     * @param file   the {@link MultipartFile} to upload
     * @return a {@link MediaResponseDTO} with the media ID, URL, and type
     */
    @PostMapping("/{postId}/media")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponseDTO uploadMedia(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file
    ) {
        var m = mediaService.uploadToPost(postId, file);
        return new MediaResponseDTO(m.getId(), m.getUrl(), m.getType().name());
    }

    /**
     * List all media attachments for a post.
     *
     * @param postId the ID of the post whose media to list
     * @return a list of {@link MediaResponseDTO}
     */
    @GetMapping("/{postId}/media")
    public List<MediaResponseDTO> listMedia(@PathVariable Long postId) {
        return mediaService.listForPost(postId).stream()
                .map(m -> new MediaResponseDTO(m.getId(), m.getUrl(), m.getType().name()))
                .toList();
    }

    /**
     * Delete a single media attachment.
     *
     * @param mediaId the ID of the media to delete
     */
    @DeleteMapping("/media/{mediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedia(@PathVariable Long mediaId) {
        mediaService.deleteMedia(mediaId);
    }
}
