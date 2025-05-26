package com.catalin.vibelog.controller;

import com.catalin.vibelog.dto.response.NotificationResponseDTO;
import com.catalin.vibelog.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for retrieving and updating user notifications.
 * <p>
 * All endpoints operate on the currently authenticated user, identified via
 * the Spring Security {@link Authentication} principal.
 * </p>
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET  /api/notifications
     * <p>
     * Return a paginated list of notifications for the current user,
     * ordered by most recent first.
     *
     * @param auth     the authentication token (supplies username)
     * @param pageable pagination & sorting parameters
     * @return a {@link Page} of {@link NotificationResponseDTO}
     */
    @GetMapping
    public Page<NotificationResponseDTO> list(
            Authentication auth,
            Pageable pageable
    ) {
        return notificationService
                .listNotifications(auth.getName(), pageable);
    }

    /**
     * GET  /api/notifications/count
     * <p>
     * Return the count of unseen notifications for the current user.
     *
     * @param auth the authentication token
     * @return a map with a single entry { "count": <unreadCount> }
     */
    @GetMapping("/count")
    public Map<String, Long> count(Authentication auth) {
        long c = notificationService.countUnread(auth.getName());
        return Map.of("count", c);
    }

    /**
     * PUT /api/notifications/read
     * <p>
     * Mark all notifications as read for the current user.
     *
     * @param auth the authentication token
     */
    @PutMapping("/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(Authentication auth) {
        notificationService.markAllRead(auth.getName());
    }

    /**
     * PUT /api/notifications/{notificationId}/read
     * <p>
     * Mark a specific notification as read for the current user.
     *
     * @param notificationId the notification to mark read
     * @param auth           the authentication token
     * @throws com.catalin.vibelog.exception.NotificationNotFoundException
     *         if the notification does not exist or does not belong to this user
     */
    @PutMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markOneRead(
            @PathVariable Long notificationId,
            Authentication auth
    ) {
        notificationService.markRead(auth.getName(), notificationId);
    }
}
