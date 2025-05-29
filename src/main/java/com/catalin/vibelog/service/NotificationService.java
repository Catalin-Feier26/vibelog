package com.catalin.vibelog.service;

import com.catalin.vibelog.dto.request.NotificationRequestDTO;
import com.catalin.vibelog.dto.response.NotificationResponseDTO;
import com.catalin.vibelog.exception.NotificationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines business operations for creating, retrieving, and updating user notifications.
 * Implementations typically interact with persistence layers and event listeners.
 */
public interface NotificationService {

    /**
     * Create and persist a new notification based on the given request.
     * Typically invoked by application event listeners when noteworthy events occur.
     *
     * @param req the {@link NotificationRequestDTO} containing notification details
     * @throws com.catalin.vibelog.exception.UserNotFoundException if the target user does not exist
     */
    void sendNotification(NotificationRequestDTO req);

    /**
     * Retrieve a paginated list of notifications for the specified user.
     *
     * @param username the username of the recipient whose notifications to list
     * @param page     pagination and sorting information
     * @return a {@link Page} of {@link NotificationResponseDTO} objects representing the user's notifications
     * @throws com.catalin.vibelog.exception.UserNotFoundException if no user exists with the given username
     */
    Page<NotificationResponseDTO> listNotifications(String username, Pageable page);

    /**
     * Count the number of unread notifications for a given user.
     *
     * @param username the username of the recipient
     * @return the count of notifications not yet marked as read
     * @throws com.catalin.vibelog.exception.UserNotFoundException if no user exists with the given username
     */
    long countUnread(String username);

    /**
     * Mark all notifications as read for the specified user.
     *
     * @param username the username of the recipient whose notifications to mark read
     * @throws com.catalin.vibelog.exception.UserNotFoundException if no user exists with the given username
     */
    void markAllRead(String username);

    /**
     * Mark a single notification as read for the specified user.
     *
     * @param username       the recipient’s username
     * @param notificationId the ID of the notification to mark as read
     * @throws NotificationNotFoundException if no matching notification exists for the user
     * @throws com.catalin.vibelog.exception.UserNotFoundException if the user does not exist
     */
    void markRead(String username, Long notificationId);
}
