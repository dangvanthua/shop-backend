package com.thuan.shop_backend.dto.response.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.constant.MessageState;
import com.thuan.shop_backend.constant.MessageType;
import com.thuan.shop_backend.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String content;
    private MessageType type;
    private MessageState state;

    @JsonProperty("sender_id")
    private long senderId;

    @JsonProperty("receiver_id")
    private long receiverId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("media_url")
    private String mediaUrl;

    public static MessageResponse fromMessage(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getMessageContent())
                .type(message.getType())
                .state(message.getState())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .createdAt(message.getSentAt())
                .mediaUrl(message.getMediaFilePath() != null ? message.getMediaFilePath() : "")
                .build();
    }
}
