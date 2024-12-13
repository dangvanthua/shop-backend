package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerInfoResponse {
    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("is_verified")
    private boolean isVerified;

    @JsonProperty("total_products_sold")
    private long totalProductsSold;

    @JsonProperty("total_reviews")
    private long totalReviews;

    @JsonProperty("registration_date")
    private LocalDateTime registrationDate;

    public static SellerInfoResponse fromSeller(
            Seller seller,
            long totalProductsSold,
            long totalReviews) {

        return SellerInfoResponse.builder()
                .storeName(seller.getStoreName())
                .email(seller.getUser().getEmail())
                .phoneNumber(seller.getUser().getPhoneNumber())
                .imageUrl(seller.getUser().getAvatar())
                .isVerified(seller.getIsVerified())
                .totalProductsSold(totalProductsSold)
                .totalReviews(totalReviews)
                .build();
    }
}
