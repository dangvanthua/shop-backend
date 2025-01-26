package com.thuan.shop_backend.dto.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.constant.MessageType;
import com.thuan.shop_backend.constant.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    @JsonProperty("chat_id")
    private long chatId;

    private String content;

    @JsonProperty("sender_id")
    private long senderId;

    @JsonProperty("receiver_id")
    private long receiverId;

    @JsonProperty("chat_name")
    private String chatName;

    @JsonProperty("message_type")
    private MessageType messageType;

    private NotificationType type;

    @JsonProperty("media_url")
    private String mediaUrl;
}
