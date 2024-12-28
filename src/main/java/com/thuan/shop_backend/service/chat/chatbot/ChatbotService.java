package com.thuan.shop_backend.service.chat.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService implements IChatbotService{

    @Value("${spring.ai.openai.base-url}")
    private String url;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Override
    public String generate(String userPrompt) {
        // Cấu hình payload để gửi yêu cầu
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "meta/llama-3.1-70b-instruct");
        payload.put("messages", List.of(
                Map.of("role", "user", "content", userPrompt)
        ));
        payload.put("temperature", 0.2);
        payload.put("top_p", 0.7);
        payload.put("max_tokens", 1024);
        payload.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Gửi yêu cầu tới NVIDIA API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                url + "/chat/completions", request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // Trả về phản hồi từ API
        } else {
            throw new RuntimeException("Error calling NVIDIA API: " + response.getStatusCode());
        }
    }
}
