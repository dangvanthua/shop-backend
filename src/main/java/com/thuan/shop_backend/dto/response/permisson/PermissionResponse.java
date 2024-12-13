package com.thuan.shop_backend.dto.response.permisson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    public static PermissionResponse fromPermission(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
