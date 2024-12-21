package com.thuan.shop_backend.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.dto.response.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("added_at")
    private LocalDate addedAt;

    @JsonProperty("product")
    private ProductResponse productResponse;
}