package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("price")
    private double price;

    @JsonProperty("stock_quantity")
    private int stockQuantity;

    @JsonProperty("attributes")
    private List<VariantAttResponse> attributes;

    public static ProductVariantResponse fromProductVariant(
            ProductVariant productVariant,
            List<VariantAttResponse> attributes) {

        return ProductVariantResponse.builder()
                .id(productVariant.getId())
                .sku(productVariant.getSku())
                .price(productVariant.getPrice())
                .stockQuantity(productVariant.getStockQuantity())
                .attributes(attributes)
                .build();
    }
}
