package com.thuan.shop_backend.service.promotion;

import com.thuan.shop_backend.dto.request.product.ProductPromotionRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionCodeRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionRequest;
import com.thuan.shop_backend.dto.response.promotion.PromotionCodeResponse;
import com.thuan.shop_backend.entity.Promotion;
import com.thuan.shop_backend.entity.PromotionCode;

import java.util.List;

public interface IPromotionService {
    Promotion createPromotion(PromotionRequest promotionRequest);
    PromotionCode createPromotionCode(long promotionId, PromotionCodeRequest promotionCodeRequest);
    void addPromotionToProduct(ProductPromotionRequest productPromotionRequest);
    List<PromotionCodeResponse> getAllPromotionCodes(List<Long> productIds);
}
