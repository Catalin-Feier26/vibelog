package com.catalin.vibelog.events;

import com.catalin.vibelog.model.enums.ReportStatus;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when a user’s report is resolved by a moderator.
 * Notifies the original reporter of outcome.
 */
public class ReportResolvedEvent extends ApplicationEvent {
    public final Long reportId;
    public final String reporterUsername;
    public final Long postId;     // null if comment-based
    public final Long commentId;  // null if post-based
    public final ReportStatus result;

    /**
     * @param source            the object publishing the event
     * @param reportId          the ID of the report record
     * @param reporterUsername  the username of who filed the report
     * @param postId            ID of the reported post (or null)
     * @param commentId         ID of the reported comment (or null)
     * @param result            the final status (REVIEWED or RESOLVED)
     */
    public ReportResolvedEvent(Object source,
                               Long reportId,
                               String reporterUsername,
                               Long postId,
                               Long commentId,
                               ReportStatus result) {
        super(source);
        this.reportId = reportId;
        this.reporterUsername = reporterUsername;
        this.postId = postId;
        this.commentId = commentId;
        this.result = result;
    }
}
