//package com.catalin.vibelog.controller;
//
//import com.catalin.vibelog.dto.request.ReblogRequestDTO;
//import com.catalin.vibelog.dto.response.ReblogResponseDTO;
//import com.catalin.vibelog.service.ReblogService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.data.domain.Sort;
//import org.springframework.web.bind.annotation.*;
//
///**
// * REST controller exposing endpoints to perform and manage reblog actions.
// */
//@RestController
//@RequestMapping("/api/reblogs")
//public class ReblogController {
//
//    private final ReblogService reblogService;
//
//    @Autowired
//    public ReblogController(ReblogService reblogService) {
//        this.reblogService = reblogService;
//    }
//
//    /**
//     * Create a new reblog entry for a user on an existing post.
//     *
//     * @param request the DTO containing reblogger's username and original post ID
//     * @return a {@link ReblogResponseDTO} describing the newly created reblog
//     */
//    @PostMapping
//    public ReblogResponseDTO reblog(
//            @Valid @RequestBody ReblogRequestDTO request
//    ) {
//        return reblogService.reblog(request);
//    }
//
//    /**
//     * Remove an existing reblog entry.
//     *
//     * @param request the DTO containing reblogger's username and original post ID
//     */
//    @DeleteMapping
//    public void undoReblog(
//            @Valid @RequestBody ReblogRequestDTO request
//    ) {
//        reblogService.undoReblog(request);
//    }
//
//    /**
//     * List all reblogs performed by the specified user, newest first.
//     *
//     * @param username the username of the reblogger
//     * @param pageable pagination and sorting parameters (default: rebloggedAt desc, size 15)
//     * @return a page of {@link ReblogResponseDTO} entries
//     */
//    @GetMapping("/{username}")
//    public Page<ReblogResponseDTO> listReblogsByUser(
//            @PathVariable String username,
//            @PageableDefault(
//                    sort = "rebloggedAt",
//                    direction = Sort.Direction.DESC,
//                    size = 15
//            )
//            Pageable pageable
//    ) {
//        return reblogService.listReblogsByUser(username, pageable);
//    }
//}
