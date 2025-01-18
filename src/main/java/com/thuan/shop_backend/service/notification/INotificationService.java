package com.thuan.shop_backend.service.notification;

import com.thuan.shop_backend.dto.response.notification.NotificationResponse;

public interface INotificationService {
    void sendNotification(long userId, NotificationResponse notificationResponse);
}
