// src/main/java/com/catalin/vibelog/controller/FollowController.java
package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.FollowStateDTO;
import com.catalin.vibelog.dto.response.FollowCountDTO;
import com.catalin.vibelog.dto.request.FollowRequestDTO;
import com.catalin.vibelog.service.FollowService;
import com.catalin.vibelog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes follow/unfollow endpoints that accept both
 * usernames in the request body.
 */
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @Autowired
    public FollowController(FollowService followService, UserService userService) {
        this.followService = followService;
        this.userService   = userService;
    }

    /**
     * Check whether {@code followerUsername} is following {@code followeeUsername}.
     *
     * @param req  the DTO containing both usernames
     * @return DTO with {@code following=true} if the relationship exists
     */
    @PostMapping("/state")
    public FollowStateDTO isFollowing(@Valid @RequestBody FollowRequestDTO req) {
        Long followerId = userService.findByUsername(req.followerUsername()).getId();
        Long followeeId = userService.findByUsername(req.followeeUsername()).getId();
        boolean following = followService.isFollowing(followerId, followeeId);
        return new FollowStateDTO(following);
    }

    /**
     * Create a follow relationship from {@code followerUsername} to {@code followeeUsername}.
     *
     * @param req  the DTO containing both usernames
     */
    @PostMapping
    public void follow(@Valid @RequestBody FollowRequestDTO req) {
        Long followerId = userService.findByUsername(req.followerUsername()).getId();
        Long followeeId = userService.findByUsername(req.followeeUsername()).getId();
        followService.follow(followerId, followeeId);
    }

    /**
     * Remove the follow relationship from {@code followerUsername} to {@code followeeUsername}.
     *
     * @param req  the DTO containing both usernames
     */
    @DeleteMapping
    public void unfollow(@Valid @RequestBody FollowRequestDTO req) {
        Long followerId = userService.findByUsername(req.followerUsername()).getId();
        Long followeeId = userService.findByUsername(req.followeeUsername()).getId();
        followService.unfollow(followerId, followeeId);
    }

    /**
     * Return follower/following counts for {@code followeeUsername}.
     *
     * @param req  the DTO; only {@code followeeUsername} is used for counts
     * @return DTO with number of {@code followers} and {@code following}
     */
    @PostMapping("/count")
    public FollowCountDTO counts(@Valid @RequestBody FollowRequestDTO req) {
        Long userId = userService.findByUsername(req.followeeUsername()).getId();
        long followers = followService.countFollowers(userId);
        long following = followService.countFollowing(userId);
        return new FollowCountDTO(followers, following);
    }
}
