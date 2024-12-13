package com.thuan.shop_backend.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Product;
import com.thuan.shop_backend.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private double price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("is_active")
    private boolean isActive;

    public static ProductResponse fromProduct(Product product, List<ProductImage> productImages) {

        String thumbnailUrl = productImages != null ? productImages.stream()
                .filter(ProductImage::getIsThumbnail)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null) : null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .thumbnail(thumbnailUrl)
                .isActive(product.getIsActive())
                .build();
    }
}
