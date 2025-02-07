package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.message.MessageRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.message.MessageResponses;
import com.thuan.shop_backend.service.message.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;

    @PostMapping
    public ApiResponse<Void> saveMessage(@RequestBody MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
        return ApiResponse.<Void>builder()
                .message("Save message success")
                .build();
    }

    @PatchMapping
    public ApiResponse<Void> setMessageToSeen(
            @RequestParam("chat-id") String chatId) {
        long chatIdParsed = Long.parseLong(chatId);
        messageService.setMessagesToSeen(chatIdParsed);
        return ApiResponse.<Void>builder()
                .message("Update message seen success")
                .build();
    }

    @GetMapping("/chat/{chat-id}")
    public ApiResponse<MessageResponses> getAllMessages(@PathVariable("chat-id") long chatId) {
        MessageResponses messageResponses = messageService.findChatMessages(chatId);
        return ApiResponse.<MessageResponses>builder()
                .message("Get all message of " + chatId + " success")
                .result(messageResponses)
                .build();
    }
}
