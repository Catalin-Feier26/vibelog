package com.catalin.vibelog.events;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a user likes a post.
 */
public class LikeEvent extends ApplicationEvent {
    public final Long postId;
    public final String likerUsername;
    public final String postAuthorUsername;

    /**
     * @param source               the object publishing the event (usually "this")
     * @param postId               the ID of the post that was liked
     * @param likerUsername        the username of the user who liked
     * @param postAuthorUsername   the username of the post’s author
     */
    public LikeEvent(Object source,
                     Long postId,
                     String likerUsername,
                     String postAuthorUsername) {
        super(source);
        this.postId = postId;
        this.likerUsername = likerUsername;
        this.postAuthorUsername = postAuthorUsername;
    }
}
