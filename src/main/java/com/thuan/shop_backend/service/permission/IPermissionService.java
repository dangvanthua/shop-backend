package com.thuan.shop_backend.service.permission;

import com.thuan.shop_backend.dto.request.PermissionRequest;
import com.thuan.shop_backend.dto.response.PermissionResponse;

import java.util.List;

public interface IPermissionService {
    PermissionResponse createPermission(PermissionRequest permissionRequest);
    List<PermissionResponse> getAllPermission();
    PermissionResponse getPermissionById(long permissionId);
    PermissionResponse updatePermission(long permissionId, PermissionRequest permissionRequest);
    void deletePermission(long permissionId);
}
