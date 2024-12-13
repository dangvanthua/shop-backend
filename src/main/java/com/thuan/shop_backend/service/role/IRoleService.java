package com.thuan.shop_backend.service.role;

import com.thuan.shop_backend.dto.request.role.RoleRequest;
import com.thuan.shop_backend.dto.response.role.RoleResponse;
import com.thuan.shop_backend.entity.Role;

import java.util.List;

public interface IRoleService {
    Role createRole(RoleRequest request);
    List<RoleResponse> getAllRoles();
    RoleResponse getRole(long roleId);
    Role updateRole(long roleId, RoleRequest roleRequest);
    void deleteRole(long roleId);
}
