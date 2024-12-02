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
public class VariantAttributeRequest {

    @JsonProperty("attribute_id")
    private long attributeId;

    @JsonProperty("value")
    private String value;
}
