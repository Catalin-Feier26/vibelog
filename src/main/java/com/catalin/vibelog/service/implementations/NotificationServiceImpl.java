package com.catalin.vibelog.service.implementations;

import com.catalin.vibelog.dto.request.NotificationRequestDTO;
import com.catalin.vibelog.dto.response.NotificationResponseDTO;
import com.catalin.vibelog.exception.NotificationNotFoundException;
import com.catalin.vibelog.model.Notification;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.repository.NotificationRepository;
import com.catalin.vibelog.service.NotificationService;
import com.catalin.vibelog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link com.catalin.vibelog.service.NotificationService}.
 * <p>
 * Delegates persistence to {@link NotificationRepository} and user lookups
 * to {@link UserService}. Provides methods to send, list, count, and mark read.
 * </p>
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserService userService;

    @Autowired
    public NotificationServiceImpl(
            NotificationRepository notificationRepo,
            UserService userService
    ) {
        this.notificationRepo = notificationRepo;
        this.userService      = userService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link Notification} from the given request DTO
     * and saves it. Throws {@link com.catalin.vibelog.exception.UserNotFoundException}
     * if the recipient username is not found.
     * </p>
     */
    @Override
    public void sendNotification(NotificationRequestDTO req) {
        User recipient = userService.findByUsername(req.recipientUsername());
        Notification n = Notification.builder()
                .type(req.type())
                .content(req.content())
                .recipient(recipient)
                .build();
        notificationRepo.save(n);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Fetches a page of {@link Notification} for the given username,
     * maps each to a {@link NotificationResponseDTO}.
     * </p>
     */
    @Override
    public Page<NotificationResponseDTO> listNotifications(
            String username,
            Pageable page
    ) {
        return notificationRepo
                .findByRecipientUsernameOrderByTimestampDesc(username, page)
                .map(n -> new NotificationResponseDTO(
                        n.getId(),
                        n.getType(),
                        n.getContent(),
                        n.isSeen(),
                        n.getTimestamp()
                ));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the number of notifications for the given user
     * that are still unseen.
     * </p>
     */
    @Override
    public long countUnread(String username) {
        return notificationRepo.countByRecipientUsernameAndSeenFalse(username);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Marks all notifications for the given user as read.
     * </p>
     */
    @Override
    public void markAllRead(String username) {
        notificationRepo.markAllReadForUser(username);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Marks the single notification as read, scoped to the given username.
     * Throws {@link NotificationNotFoundException} if no match.
     * </p>
     */
    @Override
    public void markRead(String username, Long notificationId) {
        int updated = notificationRepo
                .markAsReadByIdAndUsername(notificationId, username);
        if (updated == 0) {
            throw new NotificationNotFoundException(notificationId);
        }
    }
}
