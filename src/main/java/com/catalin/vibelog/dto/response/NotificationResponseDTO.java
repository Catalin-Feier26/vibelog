package com.catalin.vibelog.dto.response;

import com.catalin.vibelog.model.enums.NotificationType;
import java.time.LocalDateTime;

/**
 * Sent to the client to render a notification.
 *
 * @param id        unique notification ID
 * @param type      event type (LIKE, COMMENT, etc.)
 * @param content   descriptive message
 * @param seen      whether the user has viewed it
 * @param timestamp when it was created
 */
public record NotificationResponseDTO(
        Long id,
        NotificationType type,
        String content,
        boolean seen,
        LocalDateTime timestamp
) { }
