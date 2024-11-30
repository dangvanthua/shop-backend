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
public class SellerResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("registration_date")
    private LocalDateTime registrationDate;

    @JsonProperty("is_verified")
    private boolean isVerified;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatar")
    private String avatar;


    public static SellerResponse fromSeller(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .storeName(seller.getStoreName())
                .registrationDate(seller.getRegistrationDate())
                .isVerified(seller.getIsVerified())
                .fullName(seller.getUser().getFullName())
                .phoneNumber(seller.getUser().getPhoneNumber())
                .email(seller.getUser().getEmail())
                .avatar(seller.getUser().getAvatar())
                .build();
    }
}
