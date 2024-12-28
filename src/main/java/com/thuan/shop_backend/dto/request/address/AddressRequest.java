package com.thuan.shop_backend.dto.request.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {
    @JsonProperty("address_line")
    private String addressLine;
    private String city;
    private String ward;
    private String district;
    private String country;
}
