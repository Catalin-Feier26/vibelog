package com.catalin.vibelog.events;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a user reblogs (reposts) another user’s post.
 */
public class ReblogEvent extends ApplicationEvent {
    public final Long originalPostId;
    public final String rebloggerUsername;
    public final String originalAuthorUsername;

    /**
     * @param source                 the object publishing the event
     * @param originalPostId         the ID of the post being reblogged
     * @param rebloggerUsername      the username of the user doing the reblog
     * @param originalAuthorUsername the username of the original post’s author
     */
    public ReblogEvent(Object source,
                       Long originalPostId,
                       String rebloggerUsername,
                       String originalAuthorUsername) {
        super(source);
        this.originalPostId = originalPostId;
        this.rebloggerUsername = rebloggerUsername;
        this.originalAuthorUsername = originalAuthorUsername;
    }
}
