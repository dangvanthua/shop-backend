package com.thuan.shop_backend.dto.request.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("quantity")
    private int quantity;
}
