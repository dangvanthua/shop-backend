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
public class PaypalPaymentRequest extends BasePaymentRequest{
    @JsonProperty("method")
    private String method;

    @JsonProperty("intent")
    private String intent;
}
