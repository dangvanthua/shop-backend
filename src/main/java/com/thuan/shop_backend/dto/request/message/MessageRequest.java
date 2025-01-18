package com.thuan.shop_backend.dto.request.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.constant.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
    private String content;

    @JsonProperty("sender_id")
    private long senderId;

    @JsonProperty("receiver_id")
    private long receiverId;

    private MessageType type;

    @JsonProperty("chat_id")
    private long chatId;
}
