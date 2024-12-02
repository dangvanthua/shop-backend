package com.thuan.shop_backend.dto.request;

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
public class ProductVariantRequest {

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("price")
    private double price;

    @JsonProperty("stock_quantity")
    private int stockQuantity;

    @JsonProperty("attributes")
    private List<VariantAttributeRequest> attributeRequests;
}
