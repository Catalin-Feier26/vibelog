package com.catalin.vibelog.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Composite key for the Follow entity, combining follower and followed user IDs.
 */
@Embeddable
@Setter
@Getter
@EqualsAndHashCode
public class FollowId implements Serializable {

    /** ID of the user who follows another user. */
    private Long followingUserId;

    /** ID of the user being followed. */
    private Long followedUserId;

    /** Default constructor for JPA. */
    public FollowId() {
    }

    /**
     * Constructs a FollowId with specified follower and followed IDs.
     *
     * @param followingUserId ID of the follower
     * @param followedUserId  ID of the followed user
     */
    public FollowId(Long followingUserId, Long followedUserId) {
        this.followingUserId = followingUserId;
        this.followedUserId = followedUserId;
    }

}
