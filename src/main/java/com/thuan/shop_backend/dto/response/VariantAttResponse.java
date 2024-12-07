package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.VariantAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantAttResponse {
    @JsonProperty("attribute_name")
    private String attributeName;

    @JsonProperty("value")
    private String value;

    public static VariantAttResponse fromVariantAttr(VariantAttribute variantAttribute) {
        return VariantAttResponse.builder()
                .attributeName(variantAttribute.getAttribute().getName())
                .value(variantAttribute.getValue())
                .build();
    }
}
