package com.thuan.shop_backend.service.message;

import com.thuan.shop_backend.dto.request.message.MessageRequest;
import com.thuan.shop_backend.dto.response.message.MessageResponse;
import com.thuan.shop_backend.dto.response.message.MessageResponses;

import java.util.List;

public interface IMessageService {
    void saveMessage(MessageRequest messageRequest);
    MessageResponses findChatMessages(long chatId);
    void setMessagesToSeen(long chatId);
}
