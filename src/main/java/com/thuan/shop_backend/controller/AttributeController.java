package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.AttributeRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.AttributeResponse;
import com.thuan.shop_backend.service.attribute.IAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final IAttributeService attributeService;

    @PostMapping
    public ApiResponse<AttributeResponse> createAttribute(
            @Valid @RequestBody AttributeRequest attributeRequest) {
        AttributeResponse attributeResponse = attributeService.createAttribute(attributeRequest);
        return ApiResponse.<AttributeResponse>builder()
                .message("Create attribute success")
                .result(attributeResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<AttributeResponse>> getAllAttributes() {
        List<AttributeResponse> attributeResponses = attributeService.getAllAttributes();
        return ApiResponse.<List<AttributeResponse>>builder()
                .message("Get all attributes success")
                .result(attributeResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AttributeResponse> getAttribute(@PathVariable("id") long attributeId) {
        AttributeResponse attributeResponse = attributeService.getAttribute(attributeId);
        return ApiResponse.<AttributeResponse>builder()
                .message("Get attribute success")
                .result(attributeResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AttributeResponse> updateAttribute(
            @PathVariable("id") long attributeId,
            @Valid @RequestBody AttributeRequest attributeRequest) {
        AttributeResponse attributeResponse = attributeService.updateAttribute(
                attributeId, attributeRequest);
        return ApiResponse.<AttributeResponse>builder()
                .message("Update attribute success")
                .result(attributeResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAttribute(@PathVariable("id") long attributeId) {
        attributeService.deleteAttribute(attributeId);
        return ApiResponse.<Void>builder()
                .message("Delete attribute success")
                .build();
    }
}
