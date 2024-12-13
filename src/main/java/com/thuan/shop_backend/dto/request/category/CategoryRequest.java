package com.thuan.shop_backend.dto.request.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    @JsonProperty("name")
    private String name;

    @JsonProperty("parent_id")
    private long parentId;

    @JsonProperty("attribute_ids")
    private List<Long> attributeIds;
}
