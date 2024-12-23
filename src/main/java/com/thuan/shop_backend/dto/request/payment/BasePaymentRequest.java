package com.thuan.shop_backend.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BasePaymentRequest {
    @JsonProperty("total")
    private double total;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cancel_url")
    private String cancelUrl;

    @JsonProperty("success_url")
    private String successUrl;
}
