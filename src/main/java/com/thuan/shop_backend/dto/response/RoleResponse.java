package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Permission;
import com.thuan.shop_backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("permissions")
    private List<PermissionResponse> permissions;

    public static RoleResponse fromRole(
            Role role,
            List<Permission> permissions) {

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissions.stream()
                        .map(PermissionResponse::fromPermission)
                        .toList())
                .build();
    }
}
