package com.thuan.shop_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
