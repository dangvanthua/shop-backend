package com.thuan.shop_backend.dto.response.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPaymentResponse {
    @JsonProperty("approve_url")
    private String approveUrl;

    @JsonProperty("order_id")
    private long orderId;
}
