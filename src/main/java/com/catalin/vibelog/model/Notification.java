package com.catalin.vibelog.model;

import com.catalin.vibelog.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Notification sent to a user in response to social interactions
 * such as likes, comments, follows, or reblogs.
 */
@Entity
@Setter
@Getter
@Table(name = "notifications")
public class Notification {

    /** Primary key identifier for the notification. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The type of event that triggered this notification. */
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    /** Descriptive content or message of the notification. */
    private String content;

    /** Flag indicating whether the user has seen this notification. */
    private boolean seen;

    /** Timestamp when the notification was created. */
    private LocalDateTime timestamp;

    /** The recipient user who will receive this notification. */
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    /** Default no-args constructor required by JPA. */
    public Notification() {}

    /**
     * Entry point for the {@link Builder} to construct a {@code Notification}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for creating {@link Notification} instances.
     */
    public static class Builder {
        private NotificationType type;
        private String content;
        private boolean seen = false;
        private LocalDateTime timestamp = LocalDateTime.now();
        private User recipient;

        /**
         * Sets the notification type.
         *
         * @param type the {@link NotificationType}
         * @return this {@code Builder} instance
         */
        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the notification content message.
         *
         * @param content the message text
         * @return this {@code Builder} instance
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * Marks whether the notification has been seen.
         *
         * @param seen true if seen, false otherwise
         * @return this {@code Builder} instance
         */
        public Builder seen(boolean seen) {
            this.seen = seen;
            return this;
        }

        /**
         * Sets the timestamp of the notification.
         *
         * @param timestamp the creation time
         * @return this {@code Builder} instance
         */
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the recipient user of the notification.
         *
         * @param recipient the {@link User} who will receive this notification
         * @return this {@code Builder} instance
         */
        public Builder recipient(User recipient) {
            this.recipient = recipient;
            return this;
        }

        /**
         * Builds and returns the configured {@link Notification} instance.
         *
         * @return the constructed {@code Notification}
         * @throws IllegalStateException if required fields are missing
         */
        public Notification build() {
            if (type == null) {
                throw new IllegalStateException("Notification type must be set");
            }
            if (recipient == null) {
                throw new IllegalStateException("Notification recipient must be set");
            }
            Notification n = new Notification();
            n.setType(this.type);
            n.setContent(this.content);
            n.setSeen(this.seen);
            n.setTimestamp(this.timestamp);
            n.setRecipient(this.recipient);
            return n;
        }
    }

}
