package com.thuan.shop_backend.dto.response.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {
    private long id;
    private String name;

    @JsonProperty("unread_count")
    private long unreadCount;

    @JsonProperty("last_message")
    private String lastMessage;

    @JsonProperty("last_message_time")
    private LocalDateTime lastMessageTime;

    @JsonProperty("sender_id")
    private long senderId;

    @JsonProperty("receiver_id")
    private long receiverId;

    @JsonProperty("chat_avatar")
    private String chatAvatar;

    public static ChatResponse fromChat(Chat chat, long senderId) {
        return ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getChatName(senderId))
                .unreadCount(chat.getUnreadMessages(senderId))
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getLastMessageTime())
                .senderId(chat.getSender().getId())
                .receiverId(chat.getRecipient().getId())
                .chatAvatar(chat.getChatAvatar(senderId))
                .build();
    }
}
