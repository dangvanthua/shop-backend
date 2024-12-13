package com.thuan.shop_backend.service.permission;

import com.thuan.shop_backend.dto.request.permission.PermissionRequest;
import com.thuan.shop_backend.dto.response.permisson.PermissionResponse;
import com.thuan.shop_backend.entity.Permission;

import java.util.List;

public interface IPermissionService {
    Permission createPermission(PermissionRequest permissionRequest);
    List<PermissionResponse> getAllPermission();
    PermissionResponse getPermissionById(long permissionId);
    Permission updatePermission(long permissionId, PermissionRequest permissionRequest);
    void deletePermission(long permissionId);
}
