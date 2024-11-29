package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.constant.DataType;
import com.thuan.shop_backend.entity.Attribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("data_type")
    private DataType dataType;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static AttributeResponse fromAttribute(Attribute attribute) {
        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .dataType(attribute.getDataType())
                .createdAt(attribute.getCreatedAt())
                .build();
    }
}
