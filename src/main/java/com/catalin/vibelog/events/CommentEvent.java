package com.catalin.vibelog.events;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a user comments on a post.
 */
public class CommentEvent extends ApplicationEvent {
    public final Long postId;
    public final Long commentId;
    public final String commenterUsername;
    public final String postAuthorUsername;

    /**
     * @param source               the object publishing the event
     * @param postId               the ID of the post that was commented on
     * @param commentId            the new comment’s ID
     * @param commenterUsername    the username of the commenter
     * @param postAuthorUsername   the username of the post’s author
     */
    public CommentEvent(Object source,
                        Long postId,
                        Long commentId,
                        String commenterUsername,
                        String postAuthorUsername) {
        super(source);
        this.postId = postId;
        this.commentId = commentId;
        this.commenterUsername = commenterUsername;
        this.postAuthorUsername = postAuthorUsername;
    }
}
