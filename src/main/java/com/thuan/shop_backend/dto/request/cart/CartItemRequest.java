package com.thuan.shop_backend.dto.request.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemRequest {
    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("promotion_code")
    private List<String> promotionCode;
}
