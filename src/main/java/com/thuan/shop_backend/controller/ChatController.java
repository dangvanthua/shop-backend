package com.thuan.shop_backend.controller;
import com.thuan.shop_backend.dto.request.chat.ChatRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.chat.ChatResponse;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.chat.IChatService;
import com.thuan.shop_backend.service.chat.chatbot.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final IChatbotService chatbotService;
    private final IChatService chatService;

    @PostMapping("/generate")
    public ApiResponse<String> generateText(@RequestBody Map<String, String> request) {
        String userPrompt = request.get("prompt");

        try {
            String generatedText = chatbotService.generate(userPrompt);
            String content = generatedText.substring(generatedText.indexOf("\"content\":\"") + 11);
            content = content.substring(0, content.indexOf("\"}"));
            return ApiResponse.<String>builder()
                    .message("Get message from chat model success")
                    .result(content)
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @PostMapping
    public ApiResponse<Long> createChat(@RequestBody ChatRequest chatRequest) {
        long chatId = chatService.createChat(chatRequest.getReceiverId());
        return ApiResponse.<Long>builder()
                .message("Create chat success")
                .result(chatId)
                .build();
    }

    @GetMapping
    public ApiResponse<List<ChatResponse>> getChatByReceiver(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        List<ChatResponse> chatResponses = chatService.getChatsByReceiverId(page, size);
        return ApiResponse.<List<ChatResponse>>builder()
                .message("Get chats by receiver success")
                .result(chatResponses)
                .build();
    }
}
