package com.catalin.vibelog.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite primary key for the {@link Like} entity,
 * combining the user ID and post ID.
 */
@Embeddable
@EqualsAndHashCode
public class LikeId implements Serializable {

    /** ID of the user who liked the post. */
    private Long userId;

    /** ID of the post that was liked. */
    private Long postId;

    /**
     * Default constructor required by JPA.
     */
    public LikeId() {
    }

    /**
     * Constructs a composite key for a like.
     *
     * @param userId ID of the user liking the post
     * @param postId ID of the liked post
     */
    public LikeId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

}
