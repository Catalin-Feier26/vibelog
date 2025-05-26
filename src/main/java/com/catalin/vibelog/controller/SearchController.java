// src/main/java/com/catalin/vibelog/controller/SearchController.java
package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.UserResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes search endpoints for users and posts.
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/search/users?q={q}
     */
    @GetMapping("/users")
    public Page<UserResponseDTO> users(
            @RequestParam("q") String q,
            Pageable pageable
    ) {
        return searchService.searchUsers(q, pageable);
    }

    /**
     * GET /api/search/posts?q={q}
     */
    @GetMapping("/posts")
    public Page<PostResponse> posts(
            @RequestParam("q") String q,
            Pageable pageable
    ) {
        return searchService.searchPosts(q, pageable);
    }
}
