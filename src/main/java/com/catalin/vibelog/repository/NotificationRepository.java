package com.catalin.vibelog.repository;

import com.catalin.vibelog.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for managing {@link Notification} entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Fetch notifications for a given recipient, newest first.
     *
     * @param username the recipient’s username
     * @param pageable paging & sorting information
     * @return page of notifications
     */
    Page<Notification> findByRecipientUsernameOrderByTimestampDesc(String username, Pageable pageable);

    /**
     * Count how many notifications are still unseen.
     *
     * @param username the recipient’s username
     * @return number of unseen notifications
     */
    long countByRecipientUsernameAndSeenFalse(String username);

    /**
     * Mark all notifications as read for a user.
     *
     * @param username the recipient’s username
     */
    @Modifying
    @Transactional
    @Query("update Notification n set n.seen = true where n.recipient.username = :username")
    void markAllReadForUser(String username);

    /**
     * Mark a single notification as read, but only if it belongs to the given user.
     *
     * @param id       the notification ID
     * @param username the recipient’s username
     * @return number of rows updated (0 if none matched)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n " +
            "SET n.seen = true " +
            "WHERE n.id = :id " +
            "  AND n.recipient.username = :username")
    int markAsReadByIdAndUsername(
            @Param("id") Long id,
            @Param("username") String username
    );
}
