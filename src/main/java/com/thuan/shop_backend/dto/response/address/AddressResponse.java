package com.thuan.shop_backend.dto.response.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private long id;
    @JsonProperty("address_line")
    private String addressLine;
    private String city;
    private String ward;
    private String district;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("country")
    private String country;

    @JsonProperty("is_default")
    private boolean isDefault;

    public static AddressResponse fromAddress(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .ward(address.getWard())
                .district(address.getDistrict())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }
}
