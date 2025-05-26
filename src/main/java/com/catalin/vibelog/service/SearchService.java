package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.response.UserResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Search operations across users and posts.
 */
public interface SearchService {

    /**
     * Search users by username fragment.
     *
     * @param q        the substring to match in usernames
     * @param pageable paging information
     * @return a page of {@link UserResponseDTO}
     */
    Page<UserResponseDTO> searchUsers(String q, Pageable pageable);

    /**
     * Search published posts by title/body fragment.
     *
     * @param q        the substring to match in title or body
     * @param pageable paging information
     * @return a page of {@link PostResponse}
     */
    Page<PostResponse> searchPosts(String q, Pageable pageable);
}
