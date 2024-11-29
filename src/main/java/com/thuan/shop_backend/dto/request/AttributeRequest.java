package com.thuan.shop_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.constant.DataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeRequest {
    @JsonProperty("name")
    private String name;

    @JsonProperty("data_type")
    private DataType dataType;

    @JsonProperty("is_variant")
    private Boolean isVariant = false;
}
