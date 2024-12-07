package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Product;
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
public class ProductDetailResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private double price;

    @JsonProperty("stock_quantity")
    private int stockQuantity;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("images")
    private List<ProductImageResponse> imageUrls;

    @JsonProperty("seller_info")
    private SellerInfoResponse sellerInfo;

    @JsonProperty("product_variants")
    private List<ProductVariantResponse> productVariants;

    public static ProductDetailResponse fromProductDetail(
            Product product,
            List<ProductImageResponse> imageUrls,
            SellerInfoResponse sellerInfo,
            List<ProductVariantResponse> productVariants) {

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getQuantity())
                .isActive(product.getIsActive())
                .imageUrls(imageUrls)
                .sellerInfo(sellerInfo)
                .productVariants(productVariants)
                .build();
    }
}
