package com.catalin.vibelog.model;

import com.catalin.vibelog.model.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A user-submitted report for content that may violate community guidelines.
 * Can refer either to a Post or to a Comment.
 */
@Entity
@Setter
@Getter
@Table(name = "reports")
public class Report {

    /** Primary key identifier for the report entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who created this report. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /** The post being reported, if applicable. */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /** The comment being reported, if applicable. */
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /** Explanation provided by the reporter describing the issue. */
    @Column(nullable = false, length = 500)
    private String reason;

    /** Current status of the report in the review workflow. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    /** Timestamp when the report was first created; set automatically. */
    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    /** Default no-args constructor for JPA. */
    public Report() {}

    /**
     * Lifecycle callback to set the report timestamp before first persist.
     */
    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
    }

    /**
     * Entry point for the {@link Builder} to construct a {@code Report}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for creating {@link Report} instances.
     */
    public static class Builder {
        private User reporter;
        private Post post;
        private Comment comment;
        private String reason;
        private ReportStatus status = ReportStatus.PENDING;
        private LocalDateTime reportedAt = LocalDateTime.now();

        /**
         * Sets the reporter of this report.
         *
         * @param reporter the {@link User} who submits the report
         * @return this {@code Builder} instance
         */
        public Builder reporter(User reporter) {
            this.reporter = reporter;
            return this;
        }

        /**
         * Sets the post being reported.
         *
         * @param post the {@link Post} under review
         * @return this {@code Builder} instance
         */
        public Builder post(Post post) {
            this.post = post;
            return this;
        }

        /**
         * Sets the comment being reported.
         *
         * @param comment the {@link Comment} under review
         * @return this {@code Builder} instance
         */
        public Builder comment(Comment comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Sets the reason for reporting.
         *
         * @param reason explanation of the violation
         * @return this {@code Builder} instance
         */
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        /**
         * Sets the initial status of the report.
         *
         * @param status the {@link ReportStatus}
         * @return this {@code Builder} instance
         */
        public Builder status(ReportStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the timestamp when the report was created.
         *
         * @param reportedAt creation time for the report
         * @return this {@code Builder} instance
         */
        public Builder reportedAt(LocalDateTime reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }

        /**
         * Builds and returns a new {@link Report} instance.
         *
         * @return the constructed {@code Report}
         * @throws IllegalStateException if required fields are missing or invalid
         */
        public Report build() {
            if (reporter == null) {
                throw new IllegalStateException("Report reporter must be provided");
            }
            if ((post == null && comment == null) || (post != null && comment != null)) {
                throw new IllegalStateException("Report must reference exactly one of post or comment");
            }
            if (reason == null || reason.isBlank()) {
                throw new IllegalStateException("Report reason must be provided");
            }
            Report r = new Report();
            r.setReporter(this.reporter);
            r.setPost(this.post);
            r.setComment(this.comment);
            r.setReason(this.reason);
            r.setStatus(this.status);
            r.setReportedAt(this.reportedAt);
            return r;
        }
    }
}
