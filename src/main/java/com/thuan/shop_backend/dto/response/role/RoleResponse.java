package com.thuan.shop_backend.dto.response.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static RoleResponse fromRole(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
