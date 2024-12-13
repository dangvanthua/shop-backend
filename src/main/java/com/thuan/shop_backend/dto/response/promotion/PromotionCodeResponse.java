package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.ProductPromotionCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionCodeResponse {
    @JsonProperty("promotion_code")
    private String promotionCode;

    @JsonProperty("code_discount_value")
    private float codeDiscountValue;

    @JsonProperty("promotion_name")
    private String promotionName;

    @JsonProperty("discount_type")
    private String discountType;

    @JsonProperty("promotion_discount_value")
    private float promotionDiscountValue;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("is_active")
    private boolean isActive;

    public static PromotionCodeResponse fromProductPromotion(ProductPromotionCode productPromotionCode) {
        return PromotionCodeResponse.builder()
                .promotionCode(productPromotionCode.getPromotionCode().getCode())
                .codeDiscountValue(productPromotionCode.getPromotionCode().getDiscountValue())
                .promotionName(productPromotionCode.getPromotionCode().getPromotion().getName())
                .discountType(productPromotionCode.getPromotionCode().getPromotion().getDiscountType())
                .promotionDiscountValue(productPromotionCode.getPromotionCode().getDiscountValue())
                .startDate(productPromotionCode.getPromotionCode().getStartDate())
                .endDate(productPromotionCode.getPromotionCode().getEndDate())
                .isActive(productPromotionCode.getPromotionCode().getIsActive())
                .build();
    }
}
