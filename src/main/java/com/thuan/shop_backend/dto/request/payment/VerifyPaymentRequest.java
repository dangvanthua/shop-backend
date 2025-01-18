package com.thuan.shop_backend.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyPaymentRequest {

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("order_id")
    private long orderId;
}
