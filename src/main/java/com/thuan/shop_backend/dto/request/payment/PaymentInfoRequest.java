package com.thuan.shop_backend.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfoRequest {
    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("wallet_provider")
    private String walletProvider;

    @JsonProperty("wallet_address")
    private String walletAddress;
}
