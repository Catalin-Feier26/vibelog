package com.catalin.vibelog.dto.request;

import com.catalin.vibelog.model.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload to create a notification (used by event listeners).
 *
 * @param type               the notification type
 * @param recipientUsername  who receives it
 * @param content            the message body
 */
public record NotificationRequestDTO(
        @NotNull NotificationType type,
        @NotBlank String recipientUsername,
        @NotBlank String content
) { }
