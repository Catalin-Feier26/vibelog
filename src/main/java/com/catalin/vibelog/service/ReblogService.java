package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.ReblogRequestDTO;
import com.catalin.vibelog.dto.response.ReblogResponseDTO;
import com.catalin.vibelog.exception.ReblogAlreadyExistsException;
import com.catalin.vibelog.exception.ReblogNotFoundException;
import com.catalin.vibelog.exception.PostNotFoundException;
import com.catalin.vibelog.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface defining business operations for “reblog” actions.
 */
public interface ReblogService {

    /**
     * Create a new reblog entry for a user on an existing post.
     *
     * @param request contains the reblogger’s username and the original post’s ID
     * @return a DTO describing the newly created reblog
     * @throws UserNotFoundException         if the reblogger username is not found
     * @throws PostNotFoundException         if the original post ID does not exist
     * @throws ReblogAlreadyExistsException  if the user has already reblogged this post
     */
    ReblogResponseDTO reblog(ReblogRequestDTO request)
            throws UserNotFoundException, PostNotFoundException, ReblogAlreadyExistsException;

    /**
     * Remove an existing reblog entry.
     *
     * @param request contains the reblogger’s username and the original post’s ID
     * @throws ReblogNotFoundException if no such reblog exists
     */
    void undoReblog(ReblogRequestDTO request)
            throws ReblogNotFoundException;

    /**
     * List all reblogs performed by a specific user, in pages.
     *
     * @param username the reblogger’s username
     * @param pageable paging and sorting parameters
     * @return a page of {@link ReblogResponseDTO} entries
     */
    Page<ReblogResponseDTO> listReblogsByUser(String username, Pageable pageable);
}
