package com.thuan.shop_backend.service.chat;

import com.thuan.shop_backend.dto.response.chat.ChatResponse;

import java.util.List;

public interface IChatService {
    List<ChatResponse> getChatsByReceiverId(int page, int size);
    long createChat(long receiverId);
}
