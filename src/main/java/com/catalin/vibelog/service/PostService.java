package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.PostRequest;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Business operations for creating, retrieving, updating, and deleting posts.
 */
public interface PostService {

    /**
     * Create a new post authored by the given user.
     *
     * @param req             the {@link PostRequest} carrying title, body, and status
     * @param authorUsername  the username of the post’s creator (from Authentication)
     * @return the created {@link PostResponse}
     */
    PostResponse createPost(PostRequest req, String authorUsername);

    /**
     * Fetch a single post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return the corresponding {@link PostResponse}
     * @throws com.catalin.vibelog.exception.PostNotFoundException if no post exists with this ID
     */
    PostResponse getPostById(Long postId);

    /**
     * List posts filtered by their {@link PostStatus}, in pages.
     *
     * @param status   the status to filter by (e.g. PUBLISHED or DRAFT)
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link PostResponse}
     */
    Page<PostResponse> listPosts(PostStatus status, Pageable pageable);

    /**
     * Update an existing post. Only the original author may perform this.
     *
     * @param postId          the ID of the post to update
     * @param req             the {@link PostRequest} with new title/body/status
     * @param authorUsername  the username of the user attempting the update
     * @return the updated {@link PostResponse}
     * @throws com.catalin.vibelog.exception.PostNotFoundException         if the post does not exist
     * @throws com.catalin.vibelog.exception.UnauthorizedActionException   if the current user is not the post’s author
     */
    PostResponse updatePost(Long postId, PostRequest req, String authorUsername);

    /**
     * Delete a post. Only the original author may perform this.
     *
     * @param postId         the ID of the post to delete
     * @param authorUsername the username of the user attempting the deletion
     * @throws com.catalin.vibelog.exception.PostNotFoundException       if the post does not exist
     * @throws com.catalin.vibelog.exception.UnauthorizedActionException if the current user is not the post’s author
     */
    void deletePost(Long postId, String authorUsername);
    /**
     * Page through posts authored by a given user, newest first.
     *
     * @param authorUsername the username whose posts to fetch
     * @param pageable       pagination information (page, size, sort)
     * @return a page of PostResponse, ordered by createdAt desc
     */
    Page<PostResponse> listPostsByAuthor(String authorUsername, Pageable pageable);

}
