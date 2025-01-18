package com.thuan.shop_backend.service.notification;

import com.thuan.shop_backend.dto.response.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService{

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotification(
            long userId,
            NotificationResponse notificationResponse) {
        String stringUserId = String.valueOf(userId);
        messagingTemplate.convertAndSendToUser(
                stringUserId,
                "/chat",
                notificationResponse);
    }
}
