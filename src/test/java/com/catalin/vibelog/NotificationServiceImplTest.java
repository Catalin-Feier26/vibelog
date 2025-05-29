package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.catalin.vibelog.dto.request.NotificationRequestDTO;
import com.catalin.vibelog.dto.response.NotificationResponseDTO;
import com.catalin.vibelog.exception.NotificationNotFoundException;
import com.catalin.vibelog.model.Notification;
import com.catalin.vibelog.model.RegularUser;
import com.catalin.vibelog.model.enums.NotificationType;
import com.catalin.vibelog.repository.NotificationRepository;
import com.catalin.vibelog.service.UserService;
import com.catalin.vibelog.service.implementations.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepo;
    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendNotification_Success() {
        // Prepare
        var req = new NotificationRequestDTO(NotificationType.LIKE, "bob", "You have a new like");
        var recipient = new RegularUser(); recipient.setUsername("bob");
        when(userService.findByUsername("bob")).thenReturn(recipient);

        // Capture saved notification
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        when(notificationRepo.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        notificationService.sendNotification(req);

        // Verify
        verify(userService).findByUsername("bob");
        verify(notificationRepo).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals(NotificationType.LIKE, saved.getType());
        assertEquals("You have a new like", saved.getContent());
        assertEquals(recipient, saved.getRecipient());
        assertFalse(saved.isSeen());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void sendNotification_WhenUserNotFound_Throws() {
        var req = new NotificationRequestDTO(NotificationType.COMMENT, "alice", "New comment");
        when(userService.findByUsername("alice")).thenThrow(new RuntimeException("Not found"));
        assertThrows(RuntimeException.class,
                () -> notificationService.sendNotification(req));
        verify(notificationRepo, never()).save(any());
    }

    @Test
    void listNotifications_ReturnsMappedPage() {
        var now = LocalDateTime.of(2023,1,1,12,0);
        Notification n = Notification.builder()
                .type(NotificationType.FOLLOW)
                .content("X followed you")
                .recipient(new RegularUser())
                .build();
        n.setId(5L);
        n.setSeen(true);
        n.setTimestamp(now);
        Pageable page = PageRequest.of(0,10);
        when(notificationRepo.findByRecipientUsernameOrderByTimestampDesc("bob", page))
                .thenReturn(new PageImpl<>(List.of(n)));

        Page<NotificationResponseDTO> result = notificationService.listNotifications("bob", page);

        assertEquals(1, result.getTotalElements());
        NotificationResponseDTO dto = result.getContent().get(0);
        assertEquals(5L, dto.id());
        assertEquals(NotificationType.FOLLOW, dto.type());
        assertEquals("X followed you", dto.content());
        assertTrue(dto.seen());
        assertEquals(now, dto.timestamp());
        verify(notificationRepo).findByRecipientUsernameOrderByTimestampDesc("bob", page);
    }

    @Test
    void countUnread_ReturnsCorrect() {
        when(notificationRepo.countByRecipientUsernameAndSeenFalse("carol")).thenReturn(3L);
        long count = notificationService.countUnread("carol");
        assertEquals(3L, count);
        verify(notificationRepo).countByRecipientUsernameAndSeenFalse("carol");
    }

    @Test
    void markAllRead_CallsRepository() {
        notificationService.markAllRead("dave");
        verify(notificationRepo).markAllReadForUser("dave");
    }

    @Test
    void markRead_WhenFound_Succeeds() {
        when(notificationRepo.markAsReadByIdAndUsername(7L, "eve")).thenReturn(1);
        notificationService.markRead("eve", 7L);
        verify(notificationRepo).markAsReadByIdAndUsername(7L, "eve");
    }

    @Test
    void markRead_WhenNotFound_ThrowsNotificationNotFoundException() {
        when(notificationRepo.markAsReadByIdAndUsername(8L, "frank")).thenReturn(0);
        assertThrows(NotificationNotFoundException.class,
                () -> notificationService.markRead("frank", 8L));
    }
}
