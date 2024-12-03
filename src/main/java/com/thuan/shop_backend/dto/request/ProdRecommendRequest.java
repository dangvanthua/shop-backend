package com.thuan.shop_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdRecommendRequest {
    private String name;
    private String description;
    private String categoryName;
    private double price;
    private int quantity;
}
