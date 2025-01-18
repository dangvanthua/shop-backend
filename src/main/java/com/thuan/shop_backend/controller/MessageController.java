package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.message.MessageRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.message.MessageResponse;
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
    public ApiResponse<Void> setMessageToSeen(@RequestParam("chat-id") long chatId) {
        messageService.setMessagesToSeen(chatId);
        return ApiResponse.<Void>builder()
                .message("Update message seen success")
                .build();
    }

    @GetMapping("/chat/{chat-id}")
    public ApiResponse<List<MessageResponse>> getAllMessages(
            @PathVariable("chat-id") long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        List<MessageResponse> messageResponses = messageService.findChatMessages(chatId, page, size);
        return ApiResponse.<List<MessageResponse>>builder()
                .message("Get all message of " + chatId + " success")
                .result(messageResponses)
                .build();
    }
}
