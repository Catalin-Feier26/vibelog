package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.UserResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing search endpoints for users and posts.
 * <p>
 * Provides paginated, case-insensitive search over usernames and post content.
 * </p>
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    /**
     * Constructs the SearchController with the required SearchService.
     *
     * @param searchService the service used to perform user and post searches
     */
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/search/users?q={q} : Search for users by username fragment.
     *
     * @param q        the query string to match against usernames (case-insensitive)
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link UserResponseDTO} matching the query
     */
    @GetMapping("/users")
    public Page<UserResponseDTO> users(
            @RequestParam("q") String q,
            Pageable pageable
    ) {
        return searchService.searchUsers(q, pageable);
    }

    /**
     * GET /api/search/posts?q={q} : Search for published posts by title or body fragment.
     *
     * @param q        the query string to match against post titles or bodies (case-insensitive)
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link PostResponse} matching the query
     */
    @GetMapping("/posts")
    public Page<PostResponse> posts(
            @RequestParam("q") String q,
            Pageable pageable
    ) {
        return searchService.searchPosts(q, pageable);
    }
}
