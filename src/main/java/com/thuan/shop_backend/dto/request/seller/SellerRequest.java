package com.thuan.shop_backend.dto.request.seller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.dto.request.payment.PaymentInfoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerRequest {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("payment_info")
    private PaymentInfoRequest paymentInfo;
}
