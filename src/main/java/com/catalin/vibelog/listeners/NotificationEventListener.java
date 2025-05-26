package com.catalin.vibelog.listeners;

import com.catalin.vibelog.dto.request.NotificationRequestDTO;
import com.catalin.vibelog.events.CommentEvent;
import com.catalin.vibelog.events.FollowEvent;
import com.catalin.vibelog.events.LikeEvent;
import com.catalin.vibelog.events.ReblogEvent;
import com.catalin.vibelog.events.ReportResolvedEvent;
import com.catalin.vibelog.model.enums.NotificationType;
import com.catalin.vibelog.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listens for social-action events (likes, comments, follows, reblogs,
 * and report resolutions) and converts them into notifications.
 * <p>
 * Each {@code @EventListener} method takes the published event,
 * builds a {@link NotificationRequestDTO} and delegates to
 * {@link NotificationService#sendNotification(NotificationRequestDTO)}.
 * </p>
 */
@Component
public class NotificationEventListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService notifService;

    /**
     * Construct a new listener with the required {@link NotificationService}.
     *
     * @param notifService the service used to persist notifications
     */
    @Autowired
    public NotificationEventListener(NotificationService notifService) {
        this.notifService = notifService;
    }

    /**
     * Handle user "like" actions.
     * <p>
     * When a {@link LikeEvent} is published, sends a LIKE notification
     * to the author of the liked post.
     * </p>
     *
     * @param ev the event containing liker, postId, and postAuthorUsername
     */
    @EventListener
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLike(LikeEvent ev) {
        log.debug("▶️ Received LikeEvent for postId={}, liker={}, author={}",
                ev.postId, ev.likerUsername, ev.postAuthorUsername);
        String content = "💖 @" + ev.likerUsername +
                " liked your post #" + ev.postId;
        notifService.sendNotification(new NotificationRequestDTO(
                NotificationType.LIKE,
                ev.postAuthorUsername,
                content
        ));
    }

    /**
     * Handle user "comment" actions.
     * <p>
     * When a {@link CommentEvent} is published, sends a COMMENT notification
     * to the author of the commented post.
     * </p>
     *
     * @param ev the event containing commenterUsername, postId, and postAuthorUsername
     */
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void handleComment(CommentEvent ev) {
        String content = "💬 @" + ev.commenterUsername +
                " commented on your post #" + ev.postId;
        notifService.sendNotification(new NotificationRequestDTO(
                NotificationType.COMMENT,
                ev.postAuthorUsername,
                content
        ));
    }

    /**
     * Handle user "follow" actions.
     * <p>
     * When a {@link FollowEvent} is published, sends a FOLLOW notification
     * to the user who was followed.
     * </p>
     *
     * @param ev the event containing followerUsername and followedUsername
     */
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void handleFollow(FollowEvent ev) {
        String content = "➕ @" + ev.followerUsername +
                " is now following you";
        notifService.sendNotification(new NotificationRequestDTO(
                NotificationType.FOLLOW,
                ev.followedUsername,
                content
        ));
    }

    /**
     * Handle user "reblog" actions.
     * <p>
     * When a {@link ReblogEvent} is published, sends a REBLOG notification
     * to the author of the original post.
     * </p>
     *
     * @param ev the event containing rebloggerUsername, originalPostId, and originalAuthorUsername
     */
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void handleReblog(ReblogEvent ev) {
        String content = "🔁 @" + ev.rebloggerUsername +
                " reblogged your post #" + ev.originalPostId;
        notifService.sendNotification(new NotificationRequestDTO(
                NotificationType.REBLOG,
                ev.originalAuthorUsername,
                content
        ));
    }

    /**
     * Handle moderator "report resolved" actions.
     * <p>
     * When a {@link ReportResolvedEvent} is published, sends a REPORT notification
     * to the user who originally filed the report.
     * </p>
     *
     * @param ev the event containing reporterUsername, postId/commentId, and result
     */
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void handleReportResolved(ReportResolvedEvent ev) {
        String target = (ev.postId != null)
                ? "post #" + ev.postId
                : "comment #" + ev.commentId;
        String content = "🚩 Your report on " + target +
                " was " + ev.result.name().toLowerCase();
        notifService.sendNotification(new NotificationRequestDTO(
                NotificationType.REPORT,
                ev.reporterUsername,
                content
        ));
    }
}
