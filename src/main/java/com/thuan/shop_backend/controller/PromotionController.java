package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.product.ProductPromotionRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionCodeRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.entity.Promotion;
import com.thuan.shop_backend.entity.PromotionCode;
import com.thuan.shop_backend.service.promotion.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final IPromotionService promotionService;

    @PostMapping
    public ApiResponse<Promotion> createPromotion(
            @RequestBody PromotionRequest promotionRequest) {
        Promotion promotion = promotionService.createPromotion(promotionRequest);
        return ApiResponse.<Promotion>builder()
                .result(promotion)
                .message("Create promotion success")
                .build();
    }

    @PostMapping("/{id}")
    public ApiResponse<PromotionCode> createPromotionCode(
            @PathVariable("id") long promotionId,
            @RequestBody PromotionCodeRequest promotionCodeRequest) {
        PromotionCode promotionCode = promotionService.createPromotionCode(promotionId, promotionCodeRequest);
        return ApiResponse.<PromotionCode>builder()
                .message("Create promotion code success")
                .result(promotionCode)
                .build();
    }

    @PostMapping("/add-promotion")
    public ApiResponse<Void> addPromotionToProduct(
            @RequestBody ProductPromotionRequest productPromotionRequest) {
        promotionService.addPromotionToProduct(productPromotionRequest);
        return ApiResponse.<Void>builder()
                .message("Create product promotion code success")
                .build();
    }
}
