package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.NotificationRequestDTO;
import com.catalin.vibelog.dto.response.NotificationResponseDTO;
import com.catalin.vibelog.exception.NotificationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Business operations around user notifications.
 */
public interface NotificationService {

    /**
     * Create and persist a notification.
     * Typically called by event listeners.
     */
    void sendNotification(NotificationRequestDTO req);

    /**
     * List all notifications for a user, paged.
     */
    Page<NotificationResponseDTO> listNotifications(String username, Pageable page);

    /**
     * Count how many notifications the user hasn't seen.
     */
    long countUnread(String username);

    /**
     * Mark all notifications as read for a user.
     */
    void markAllRead(String username);
    /**
     * Mark a single notification as read.
     *
     * @param username       the recipient’s username
     * @param notificationId the ID of the notification to mark read
     * @throws NotificationNotFoundException if no matching notification exists
     */
    void markRead(String username, Long notificationId);
}
