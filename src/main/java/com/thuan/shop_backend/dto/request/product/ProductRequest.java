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
public class ProductRequest {
    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private double price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("category_id")
    private long categoryId;

    @JsonProperty("seller_id")
    private long sellerId;
}
