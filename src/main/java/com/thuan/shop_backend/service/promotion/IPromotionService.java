package com.thuan.shop_backend.service.promotion;

import com.thuan.shop_backend.dto.request.product.ProductPromotionRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionCodeRequest;
import com.thuan.shop_backend.dto.request.promotion.PromotionRequest;
import com.thuan.shop_backend.entity.Promotion;
import com.thuan.shop_backend.entity.PromotionCode;

public interface IPromotionService {
    Promotion createPromotion(PromotionRequest promotionRequest);
    PromotionCode createPromotionCode(long promotionId, PromotionCodeRequest promotionCodeRequest);
    void addPromotionToProduct(ProductPromotionRequest productPromotionRequest);

}
