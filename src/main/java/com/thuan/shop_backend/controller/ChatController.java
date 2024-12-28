package com.thuan.shop_backend.controller;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.chat.chatbot.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final IChatbotService chatbotService;

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
}
