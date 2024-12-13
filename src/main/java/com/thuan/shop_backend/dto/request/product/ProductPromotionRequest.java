package com.thuan.shop_backend.dto.request.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPromotionRequest {
    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("code")
    private String code;
}
