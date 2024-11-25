package com.thuan.shop_backend.service.role;

import com.thuan.shop_backend.dto.request.RoleRequest;
import com.thuan.shop_backend.dto.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse createRole(RoleRequest request);
    List<RoleResponse> getAllRoles();
    RoleResponse getRole(long roleId);
    RoleResponse updateRole(long roleId, RoleRequest roleRequest);
    void deleteRole(long roleId);
}
