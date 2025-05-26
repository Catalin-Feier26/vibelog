package com.catalin.vibelog.events;

import org.springframework.context.ApplicationEvent;

/**
 * Event published when a user follows another user.
 */
public class FollowEvent extends ApplicationEvent {
    public final String followerUsername;
    public final String followedUsername;

    /**
     * @param source             the object publishing the event
     * @param followerUsername   the username of the user who just followed
     * @param followedUsername   the username of the user being followed
     */
    public FollowEvent(Object source,
                       String followerUsername,
                       String followedUsername) {
        super(source);
        this.followerUsername = followerUsername;
        this.followedUsername = followedUsername;
    }
}
